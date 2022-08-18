JAVAC := "$(JAVA_HOME)/bin/javac.exe"
JAR := "$(JAVA_HOME)/bin/jar.exe"

SRC_DIR := ./src
BUILD_DIR := ./build
CLASS_DIR := $(BUILD_DIR)/classes
JAR_DIR := $(BUILD_DIR)/jar
TARGET_JAR := sweet.jar

SRCS := $(shell find $(SRC_DIR) -name *.java)
MAIN_CLASS := Sweet

$(JAR_DIR)/$(TARGET_JAR): compile
	mkdir -p $(dir $@)
	$(JAR) --verbose --create --file $@ --main-class $(MAIN_CLASS) -C $(CLASS_DIR) .

compile: $(SRCS)
	$(JAVAC) -d $(CLASS_DIR) $(SRCS)

clean:
	rm -r $(BUILD_DIR)