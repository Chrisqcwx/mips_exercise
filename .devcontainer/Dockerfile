FROM mcr.microsoft.com/devcontainers/base:jammy
# Install the xz-utils package
RUN export DEBIAN_FRONTEND=noninteractive \
    && cp -a /etc/apt/sources.list /etc/apt/sources.list.bak \
    && printf "deb https://mirrors.sustech.edu.cn/ubuntu-ports/ jammy main restricted universe multiverse\n# deb-src https://mirrors.sustech.edu.cn/ubuntu-ports/ jammy main restricted universe multiverse\ndeb https://mirrors.sustech.edu.cn/ubuntu-ports/ jammy-updates main restricted universe multiverse\n# deb-src https://mirrors.sustech.edu.cn/ubuntu-ports/ jammy-updates main restricted universe multiverse\ndeb https://mirrors.sustech.edu.cn/ubuntu-ports/ jammy-backports main restricted universe multiverse\n# deb-src https://mirrors.sustech.edu.cn/ubuntu-ports/ jammy-backports main restricted universe multiverse\ndeb https://mirrors.sustech.edu.cn/ubuntu-ports/ jammy-security main restricted universe multiverse\n# deb-src https://mirrors.sustech.edu.cn/ubuntu-ports/ jammy-security main restricted universe multiverse\n\n# deb https://mirrors.sustech.edu.cn/ubuntu-ports/ jammy-proposed main restricted universe multiverse\n# deb-src https://mirrors.sustech.edu.cn/ubuntu-ports/ jammy-proposed main restricted universe multiverse" > /etc/apt/sources.list \
    && sed -i "s@http://.*archive.ubuntu.com@https://mirrors.sustech.edu.cn@g" /etc/apt/sources.list \
    && sed -i "s@http://.*security.ubuntu.com@https://mirrors.sustech.edu.cn@g" /etc/apt/sources.list \
    && apt-get update \
    && apt install -y python3-pip \
    && pip install --upgrade pip --index-url https://mirrors.sustech.edu.cn/pypi/simple \
    && pip config set global.index-url https://mirrors.sustech.edu.cn/pypi/simple \
    && apt install -y build-essential bison texinfo iverilog gtkwave verilator scala