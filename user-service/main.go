package main

import (
	"context"
	"github.com/gobuffalo/packr/v2"
	"github.com/labstack/echo/v4"
	"github.com/labstack/echo/v4/middleware"
	com_codenotfound_grpc_helloworld "github.com/nkonev/user-service/grpc"
	grpc_server "github.com/nkonev/user-service/grpc-server"
	"github.com/nkonev/user-service/handlers"
	"github.com/nkonev/user-service/utils"
	"github.com/openzipkin/zipkin-go/reporter"
	log "github.com/sirupsen/logrus"

	"github.com/openzipkin/zipkin-go"
	zipkingrpc "github.com/openzipkin/zipkin-go/middleware/grpc"
	reporterhttp "github.com/openzipkin/zipkin-go/reporter/http"

	"github.com/spf13/viper"
	"go.uber.org/dig"
	"google.golang.org/grpc"
	"net"
	"net/http"
	"os"
	"os/signal"
	"strings"
)

func configureEcho(fsh *handlers.FsHandler) *echo.Echo {
	bodyLimit := viper.GetString("server.echo.body.limit")

	static := packr.New("static", "./static")

	e := echo.New()

	e.Use(middleware.Logger())
	e.Use(middleware.Secure())
	e.Use(middleware.BodyLimit(bodyLimit))

	e.GET("/ls", fsh.LsHandler)

	e.Pre(getStaticMiddleware(static))

	return e
}

func getStaticMiddleware(box *packr.Box) echo.MiddlewareFunc {
	return func(next echo.HandlerFunc) echo.HandlerFunc {
		return func(c echo.Context) error {
			reqUrl := c.Request().RequestURI
			if reqUrl == "/" || reqUrl == "/index.html" || reqUrl == "/admin_index.html" || reqUrl == "/favicon.ico" || strings.HasPrefix(reqUrl, "/build") || strings.HasPrefix(reqUrl, "/test-assets") {
				http.FileServer(box).
					ServeHTTP(c.Response().Writer, c.Request())
				return nil
			} else {
				return next(c)
			}
		}
	}
}

func newTracer() (*zipkin.Tracer, error) {
	// The reporter sends traces to zipkin server
	endpointURL := viper.GetString("zipkin.endpoint")
	var reporter0 reporter.Reporter
	if endpointURL == "" {
		reporter0 = reporter.NewNoopReporter()
	} else {
		reporter0 = reporterhttp.NewReporter(endpointURL)
	}

	// Sampler tells you which traces are going to be sampled or not. In this case we will record 100% (1.00) of traces.
	sampler, err := zipkin.NewCountingSampler(1)
	if err != nil {
		return nil, err
	}

	t, err := zipkin.NewTracer(
		reporter0,
		zipkin.WithSampler(sampler),
	)
	if err != nil {
		return nil, err
	}

	return t, err
}

func main() {
	log.SetOutput(os.Stdout)
	log.SetReportCaller(true)
	log.SetLevel(log.InfoLevel)

	utils.InitViper("./config-dev/config.yml")
	container := dig.New()
	container.Provide(configureHandler)
	container.Provide(configureEcho)
	container.Provide(newTracer)

	if echoErr := container.Invoke(runServers); echoErr != nil {
		log.Fatalf("Error during invoke echo: %v", echoErr)
	}
	log.Infof("Exit program")
}

func configureHandler() *handlers.FsHandler {
	return handlers.NewFsHandler()
}

// rely on viper import and it's configured by
func runServers(e *echo.Echo, tracer *zipkin.Tracer) {
	address := viper.GetString("server.echo.address")
	shutdownTimeout := viper.GetDuration("server.echo.shutdown.timeout")

	log.Info("Starting server...")
	// Start server in another goroutine
	go func() {
		if err := e.Start(address); err != nil {
			log.Infof("shutting down the echo server due error %v", err)
		}
	}()

	grpcServer := grpc.NewServer(
		grpc.StatsHandler(zipkingrpc.NewServerHandler(tracer)),
	)
	go func() {
		grpcAddress := viper.GetString("server.grpc.address")
		log.Infof("Starting grpc server on %v", grpcAddress)

		// https://medium.com/@popa.alex1/starting-a-rpc-server-in-golang-using-grpc-and-protocol-buffers-v3-6eef409b2c9a
		// https://medium.com/pantomath/how-we-use-grpc-to-build-a-client-server-system-in-go-dd20045fa1c2
		// create a listener on TCP port 5555
		lis, err := net.Listen("tcp", grpcAddress)
		if err != nil {
			log.Fatalf("failed to listen grpc port: %v", err)
		}
		// create a server instance
		s := grpc_server.Server{}

		// create a gRPC server object
		com_codenotfound_grpc_helloworld.RegisterHelloServiceServer(grpcServer, &s)
		// start the server
		if err := grpcServer.Serve(lis); err != nil {
			log.Infof("shutting down the grpc server due error %v", err)
		}
		log.Infof("Grpc server stopped")
	}()

	log.Info("Server started. Waiting for interrupt (2) (Ctrl+C)")
	// Wait for interrupt signal to gracefully shutdown the server with
	// a timeout of 10 seconds.
	quit := make(chan os.Signal)
	signal.Notify(quit, os.Interrupt)

	<-quit

	log.Infof("Got signal %v - will forcibly close after %v", os.Interrupt, shutdownTimeout)
	ctx, cancel := context.WithTimeout(context.Background(), shutdownTimeout)
	defer cancel() // releases resources if slowOperation completes before timeout elapses

	if err := e.Shutdown(ctx); err != nil {
		log.Fatal(err)
	} else {
		log.Infof("Server successfully shut down")
	}

	log.Infof("Stopping grpc server")
	grpcServer.GracefulStop()
	log.Infof("Grpc server successfully shut down")
}
