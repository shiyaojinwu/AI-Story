package model

import "time"

type Shot struct {
	ID         uint      `json:"id" gorm:"primaryKey"`
	StoryID    uint      `json:"storyId" gorm:"index"` // 加索引，增加查询速率
	Order      int       `json:"sortOrder" gorm:"column:sort_order"`
	Title      string    `json:"title"`
	Prompt     string    `json:"prompt" gorm:"type:text"`
	Narration  string    `json:"narration" gorm:"type:text"`
	Transition string    `json:"transition"`
	Status     string    `json:"status" gorm:"default:'pending'"`
	ImageURL   string    `json:"imageUrl"`
	CreatedAt  time.Time `json:"createdAt"`
	UpdatedAt  time.Time `json:"updatedAt"`
}

func (Shot) TableName() string {
	return "shots"
}
