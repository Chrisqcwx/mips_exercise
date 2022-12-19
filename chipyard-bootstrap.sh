#! /bin/bash

INIT_SUBM_SCRIPT=./scripts/init-submodules-no-riscv-tools.sh

set -e

mkdir -p ~/DevTools
cd ~/DevTools
if ! ([[-f chipyard/${INIT_SUBM_SCRIPT}]] || (cd chipyard && git fsck --full)); then
    rm -rf ./chipyard
    git clone --depth 1 --branch 1.8.1 https://github.com/ucb-bar/chipyard.git
fi
cd chipyard
${INIT_SUBM_SCRIPT}