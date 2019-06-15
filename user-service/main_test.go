package main

import (
	"github.com/labstack/echo/v4"
	"github.com/nkonev/user-service/utils"
	log "github.com/sirupsen/logrus"
	"github.com/stretchr/testify/assert"
	"go.uber.org/dig"
	"io"
	"net/http"
	test "net/http/httptest"
	"os"
	"testing"
)

func TestMain(m *testing.M) {
	setup()
	retCode := m.Run()
	shutdown()
	os.Exit(retCode)
}

func shutdown() {
	log.Info("Shutting down")
}

func setup() {
	utils.InitViper("./config-dev/config.yml")

	log.Info("Set up")
}

func request(method, path string, body io.Reader, e *echo.Echo, sessionCookie string) (int, string, http.Header) {
	req := test.NewRequest(method, path, body)
	Header := map[string][]string{
		echo.HeaderContentType: {"application/json"},
	}
	req.Header = Header
	rec := test.NewRecorder()
	e.ServeHTTP(rec, req)
	return rec.Code, rec.Body.String(), rec.HeaderMap
}

func runTest(container *dig.Container, test func(e *echo.Echo)) {

	if err := container.Invoke(func(e *echo.Echo) {
		defer e.Close()

		test(e)
	}); err != nil {
		panic(err)
	}
}

func setUpContainerForIntegrationTests() *dig.Container {
	container := dig.New()

	container.Provide(configureHandler)
	container.Provide(configureEcho)

	return container
}

func TestLs(t *testing.T) {
	container := setUpContainerForIntegrationTests()

	runTest(container, func(e *echo.Echo) {
		c, b, _ := request("GET", "/ls", nil, e, "sess-cookie-1")
		assert.Equal(t, http.StatusOK, c)
		assert.NotEmpty(t, b)
		log.Infof("Got body: %v", b)
	})
}
