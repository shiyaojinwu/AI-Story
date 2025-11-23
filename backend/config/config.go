package config

type Config struct {
	Port int
}

func LoadConfig() *Config {
	return &Config{Port: 8080}
}
