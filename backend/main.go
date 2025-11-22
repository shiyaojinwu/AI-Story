package main

import (
    "fmt"
    "log"
    "net/http"
    "os"

    "github.com/gin-gonic/gin"
    "github.com/joho/godotenv"
    "gorm.io/driver/postgres"
    "gorm.io/gorm"
)

func main() {
    // 1. 加载 .env 文件
    err := godotenv.Load()
    if err != nil {
        log.Println("⚠️  No .env file found or failed to load")
    }

    // 2. 从环境变量读取
    dsn := fmt.Sprintf(
        "host=%s user=%s password=%s dbname=%s port=%s sslmode=%s TimeZone=%s",
        os.Getenv("DB_HOST"),
        os.Getenv("DB_USER"),
        os.Getenv("DB_PASSWORD"),
        os.Getenv("DB_NAME"),
        os.Getenv("DB_PORT"),
        os.Getenv("DB_SSLMODE"),
        os.Getenv("DB_TIMEZONE"),
    )

    log.Println("DSN:", dsn)

    // 3. 测试数据库连接
    db, err := gorm.Open(postgres.Open(dsn), &gorm.Config{})
    if err != nil {
        log.Fatal("❌ Failed to connect to PostgreSQL:", err)
    }

    log.Println("✅ Connected to PostgreSQL successfully!")

    // 4. 测试接口
    r := gin.Default()

    r.GET("/ping", func(c *gin.Context) {
        c.JSON(http.StatusOK, gin.H{"message": "pong"})
    })

    // DB 测试
    r.GET("/db-status", func(c *gin.Context) {
        sqlDB, _ := db.DB()
        if err := sqlDB.Ping(); err != nil {
            c.JSON(500, gin.H{"db": "not ok"})
            return
        }
        c.JSON(200, gin.H{"db": "ok"})
    })

    r.Run(":8080")
}

