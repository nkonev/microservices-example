package utils

import (
	"flag"
	"fmt"
	"github.com/spf13/viper"
)

type H map[string]interface{}

func InitViper(defaultLocation string) {
	configFile := flag.String("config", defaultLocation, "Path to config file")
	flag.Parse()
	viper.SetConfigFile(*configFile)
	// call multiple times to add many search paths
	viper.SetEnvPrefix("BLOG_STORE")
	viper.AutomaticEnv()
	// Find and read the config file
	if err := viper.ReadInConfig(); err != nil { // Handle errors reading the config file
		panic(fmt.Errorf("Fatal error config file: %s \n", err))
	}
}
