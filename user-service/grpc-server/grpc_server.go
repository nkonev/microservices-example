package grpc_server

import (
	"github.com/labstack/gommon/log"
	com_codenotfound_grpc_helloworld "github.com/nkonev/user-service/grpc"
	"golang.org/x/net/context"
)

// Server represents the gRPC server
type Server struct {
}

// SayHello generates response to a Ping request
func (s *Server) Hello(ctx context.Context, in *com_codenotfound_grpc_helloworld.HelloRequest) (*com_codenotfound_grpc_helloworld.HelloResponse, error) {
	log.Printf("Received message %s %s", in.FirstName, in.LastName)
	return &com_codenotfound_grpc_helloworld.HelloResponse{Greeting: "Hello " + in.FirstName + " " + in.LastName}, nil
}
