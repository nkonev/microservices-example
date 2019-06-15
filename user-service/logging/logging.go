package logging

import (
	"context"
	"github.com/openzipkin/zipkin-go"
	log "github.com/sirupsen/logrus"
)

const ContextKeyTraceId = "traceId"

func WrapLoggerFromCtx(ctx context.Context, logger log.FieldLogger) log.FieldLogger {
	span := zipkin.SpanOrNoopFromContext(ctx)
	traceId := span.Context().TraceID.String()

	if logger == nil {
		logger = log.StandardLogger()
	}

	return logger.WithField(ContextKeyTraceId, traceId)
}

func GetLoggerFromCtx(ctx context.Context) log.FieldLogger {

	return WrapLoggerFromCtx(ctx, nil)
}
