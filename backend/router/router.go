package router

import (
	"github.com/gin-gonic/gin"
)

func InitRouter(db interface{}) *gin.Engine {
	r := gin.Default()
	return r
}
