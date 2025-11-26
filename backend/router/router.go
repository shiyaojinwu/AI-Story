package router

import (
	"story-video-backend/controller"

	"github.com/gin-contrib/cors"
	"github.com/gin-gonic/gin"
)

func InitRouter() *gin.Engine {
	r := gin.Default()

	//跨域访问
	config := cors.Config{
		AllowAllOrigins:  true,
		AllowMethods:     []string{"GET", "POST", "PUT", "PATCH", "DELETE", "OPPTIONS"},
		AllowHeaders:     []string{"Origin", "Content-Type", "Accept", "Authorization"},
		ExposeHeaders:    []string{"Content-Length"},
		AllowCredentials: true,
	}
	r.Use(cors.New(config))
	//api分组
	api := r.Group("/api")
	{
		api.POST("/story", controller.CreateStory)

		//shot模块
		//获取分镜列表
		api.GET("/story/:id/shots", controller.GetShotsByStory)
		//获取分镜详情
		api.GET("/shot/:id", controller.GetShotDetail)
		//更新分镜
		api.POST("/shot/:id/update", controller.UpdateShot)
		//生成mock数据
		api.POST("/story/:id/mock-gen", controller.GenMockShots)
	}

	return r
}
