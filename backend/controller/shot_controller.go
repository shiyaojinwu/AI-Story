package controller

import (
	"net/http"
	"story-video-backend/db"
	"story-video-backend/model"
	"strconv"

	"github.com/gin-gonic/gin"
)

//to do 按故事获取分镜

func GetShotsByStory(c *gin.Context) {
	storyIdStr := c.Param("id")

	var shots []model.Shot
	//数据库查询
	result := db.DB.Where("story_id = ?", storyIdStr).Order("sort_order asc").Find(&shots)

	if result.Error != nil {
		c.JSON(http.StatusInternalServerError, gin.H{
			"code":    500,
			"message": "查询分镜失败：" + result.Error.Error(),
			"data":    nil,
		})
		return
	}

	//返回数据
	c.JSON(http.StatusOK, gin.H{
		"code":    200,
		"message": "success",
		"data": gin.H{
			"storyId": storyIdStr,
			"shots":   shots,
		},
	})

}
func GetShotDetail(c *gin.Context) {
	id := c.Param("id")
	var shot model.Shot

	//根据id查询
	if err := db.DB.First(&shot, "id = ?", id).Error; err != nil {
		c.JSON(http.StatusNotFound, gin.H{
			"code":    404,
			"message": "分镜不存在" + err.Error(),
			"data":    nil,
		})
		return
	}

	c.JSON(http.StatusOK, gin.H{
		"code":    200,
		"message": "success",
		"data":    shot,
	})
}

func GenMockShots(c *gin.Context) {
	storyIdStr := c.Param("id")
	idInt, err := strconv.Atoi(storyIdStr)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{
			"code":    400,
			"message": "无效的storyId",
			"data":    nil,
		})
		return
	}
	storyId := uint(idInt)

	mockShots := []model.Shot{
		{
			StoryID: storyId, Order: 1, Title: "城市夜景", Status: 2,
			Prompt:   "Cyberpunk city night view...",
			ImageURL: "https://www.bing.com/images/search?q=%e5%9b%be%e7%89%87&id=457EC80FCD5EE9AB67B2B3E8F5624312D6F6400B&FORM=IACFIR ",
		},
		{
			StoryID: storyId, Order: 2, Title: "主角背影", Status: 2,
			Prompt:   "Hero back view... ",
			ImageURL: "https://www.bing.com/images/search?q=%e5%9b%be%e7%89%87&id=822363F23ADD7A8BEE0FEC29EF03BC9873E7B472&FORM=IACFIR ",
		},
		{
			StoryID: storyId, Order: 3, Title: "代码特写", Status: 1,
			Prompt:   "Coding screen... ",
			ImageURL: "",
		},
		{
			StoryID: storyId, Order: 4, Title: "警报响起", Status: 0,
			Prompt:   "Red alert... ",
			ImageURL: "",
		},
	}
	//批量写入数据库
	if err := db.DB.Create(&mockShots).Error; err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{
			"code":    500,
			"message": "mock数据生成失败" + err.Error(),
			"data":    nil,
		})
		return
	}
	c.JSON(http.StatusOK, gin.H{
		"code":    200,
		"message": "mock数据生成成功！调用查询接口查看",
		"data":    nil,
	})
}

// UpdateShotRequest 更新请求体结构
type UpdateShotRequest struct {
	Prompt     string `json:"prompt"`
	Narration  string `json:"narration"`
	Transition string `json:"transition"`
}

func UpdateShot(c *gin.Context) {
	id := c.Param("id")
	var req UpdateShotRequest

	// 绑定参数
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"code": 400, "message": "参数错误", "data": nil})
		return
	}

	// 构造更新数据 Map (只更新传进来的字段)
	updates := map[string]interface{}{
		"prompt":     req.Prompt,
		"narration":  req.Narration,
		"transition": req.Transition,
		"status":     0,
	}

	// 执行更新
	if err := db.DB.Model(&model.Shot{}).Where("id = ?", id).Updates(updates).Error; err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"code": 500, "message": "更新失败", "data": nil})
		return
	}

	c.JSON(http.StatusOK, gin.H{
		"code":    200,
		"message": "更新成功",
		"data": gin.H{
			"status": 0,
		},
	})
}
