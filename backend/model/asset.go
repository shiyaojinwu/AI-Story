package model

import "time"

type Asset struct {
	ID           uint      `json:"id" gorm:"primaryKey"`
	StoryID      uint      `json:"storyId" gorm:"index"`
	Title        string    `json:"title"`
	VideoURL     string    `json:"videoUrl"`
	ThumbnailURL string    `json:"thumbnailUrl"`
	Duration     int       `json:"duration"`
	Status       int       `json:"status"`
	CreatedAt    time.Time `json:"createdAt"`
}

func (Asset) TableName() string {
	return "assets"
}
