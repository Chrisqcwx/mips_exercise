BUILD_DIR = ./build

export PATH := $(PATH):$(abspath ./utils)

test:
	./millw -i __.test

verilog:
	mkdir -p $(BUILD_DIR)
	./millw -i __.test.runMain Elaborate -td $(BUILD_DIR)

help:
	./millw -i __.test.runMain Elaborate --help

compile:
	./millw -i __.compile

bsp:
	./millw -i mill.bsp.BSP/install

reformat:
	./millw -i __.reformat

checkformat:
	./millw -i __.checkFormat

clean:
	-rm -rf $(BUILD_DIR)

.PHONY: test verilog help compile bsp reformat checkformat clean
