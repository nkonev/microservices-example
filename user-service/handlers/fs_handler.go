package handlers

import (
	"github.com/labstack/echo/v4"
	"github.com/nkonev/user-service/utils"
	log "github.com/sirupsen/logrus"
	"net/http"
)

func NewFsHandler() *FsHandler {
	return &FsHandler{}
}

type FsHandler struct {
}

func (h *FsHandler) LsHandler(c echo.Context) error {
	log.Debugf("ls")

	return c.JSON(http.StatusOK, &utils.H{"status": "ok"})
}
