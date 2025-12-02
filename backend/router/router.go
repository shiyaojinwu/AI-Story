package router

import (
	"story-video-backend/controller"

	"github.com/gin-contrib/cors"
	"github.com/gin-gonic/gin"
)

func InitRouter() *gin.Engine {
	r := gin.Default()

	// 跨域访问
	config := cors.Config{
		AllowAllOrigins:  true,
		AllowMethods:     []string{"GET", "POST", "PUT", "PATCH", "DELETE", "OPPTIONS"},
		AllowHeaders:     []string{"Origin", "Content-Type", "Accept", "Authorization"},
		ExposeHeaders:    []string{"Content-Length"},
		AllowCredentials: true,
	}
	r.Use(cors.New(config))
	// api分组
	api := r.Group("/api")
	{
		// story模块
		api.POST("/story/create", controller.CreateStory)
		api.GET("/story/:id", controller.GetStoryDetail)

		// shot模块
		// 获取分镜列表
		api.GET("/story/:id/shots", controller.GetShotsByStory)
		// 获取分镜详情
		api.GET("/shot/:id", controller.GetShotDetail)
		// 更新分镜
		api.POST("/shot/:id/update", controller.UpdateShot)
		// 获取单张分镜进度
		api.GET("/shot/:id/preview", controller.GetShotProgress)
		// 生成mock数据
		api.POST("/story/:id/mock-gen", controller.GenMockShots)

		// asset模块
		api.GET("/story/all", controller.GetAllAssets)
	}

	return r
}
