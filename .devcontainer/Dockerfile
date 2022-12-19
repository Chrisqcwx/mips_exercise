FROM mcr.microsoft.com/devcontainers/base:jammy
# Install the xz-utils package
RUN export DEBIAN_FRONTEND=noninteractive \
    && cp -a /etc/apt/sources.list /etc/apt/sources.list.bak \
    && printf "deb https://mirrors.sustech.edu.cn/ubuntu-ports/ jammy main restricted universe multiverse\n# deb-src https://mirrors.sustech.edu.cn/ubuntu-ports/ jammy main restricted universe multiverse\ndeb https://mirrors.sustech.edu.cn/ubuntu-ports/ jammy-updates main restricted universe multiverse\n# deb-src https://mirrors.sustech.edu.cn/ubuntu-ports/ jammy-updates main restricted universe multiverse\ndeb https://mirrors.sustech.edu.cn/ubuntu-ports/ jammy-backports main restricted universe multiverse\n# deb-src https://mirrors.sustech.edu.cn/ubuntu-ports/ jammy-backports main restricted universe multiverse\ndeb https://mirrors.sustech.edu.cn/ubuntu-ports/ jammy-security main restricted universe multiverse\n# deb-src https://mirrors.sustech.edu.cn/ubuntu-ports/ jammy-security main restricted universe multiverse\n\n# deb https://mirrors.sustech.edu.cn/ubuntu-ports/ jammy-proposed main restricted universe multiverse\n# deb-src https://mirrors.sustech.edu.cn/ubuntu-ports/ jammy-proposed main restricted universe multiverse" > /etc/apt/sources.list \
    && sed -i "s@http://.*archive.ubuntu.com@https://mirrors.sustech.edu.cn@g" /etc/apt/sources.list \
    && sed -i "s@http://.*security.ubuntu.com@https://mirrors.sustech.edu.cn@g" /etc/apt/sources.list \
    && apt-get update \
    && apt install -y vim git curl python3-pip \
    && pip install --upgrade pip --index-url https://mirrors.sustech.edu.cn/pypi/simple \
    && pip config set global.index-url https://mirrors.sustech.edu.cn/pypi/simple \
    && apt install -y autoconf automake autotools-dev device-tree-compiler libmpc-dev libmpfr-dev libgmp-dev gawk build-essential bison flex texinfo gperf libtool patchutils bc zlib1g-dev iverilog gtkwave verilator yosys scala \
    && echo "deb https://repo.scala-sbt.org/scalasbt/debian all main" | tee /etc/apt/sources.list.d/sbt.list \
    && echo "deb https://repo.scala-sbt.org/scalasbt/debian /" | tee /etc/apt/sources.list.d/sbt_old.list \
    && curl -sL "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x2EE0EA64E40A89B84B2DF73499E82A75642AC823" | apt-key add \
    && apt update \
    && apt install sbt \
    && curl -L https://github.com/com-lihaoyi/mill/releases/download/0.11.0-M1/0.11.0-M1 > mill && chmod +x mill