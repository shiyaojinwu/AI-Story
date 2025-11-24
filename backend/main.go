package main

import (
	"fmt"
	"log"
	"os"

	"github.com/joho/godotenv"

	"story-video-backend/config"
	"story-video-backend/db"
	"story-video-backend/router"
)

func main() {
	if err := godotenv.Load(); err != nil {
		log.Println("can find file .env")
	}
	cfg := config.LoadConfig()
	database := db.InitDB(cfg)
	r := router.InitRouter(database)

	//for test
	fmt.Printf("\n \033[32mTEST config.go test\033[0m,%+v", cfg)
	fmt.Printf("\n \033[32mTEST db.go test\033[0m,%+v\n", database)

	//start the service and print port
	address := fmt.Sprintf(":%d", cfg.Port)
	fmt.Printf("Server started at http://localhost%s\n", address)

	if err := r.Run(address); err != nil {
		log.Fatalf("启动服务失败: %v", err)
		os.Exit(1)
	}

}
