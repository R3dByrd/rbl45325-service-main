#!/usr/bin/env bash
#
# Setup‑Skript für „Codex“‑Dev‑Environment (Linux/macOS)
# führt Installation/Upgrade per APT (Debian/Ubuntu) bzw. Homebrew (macOS)
# --------------------------------------------------------------------------

set -euo pipefail

# ------- Hilfsfunktionen ---------------------------------------------------
log()   { printf "\033[1;32m[+] %s\033[0m\n" "$*"; }
ask()   { read -rp "$(printf "\033[1;34m[?] %s\033[0m " "$*")" REPLY; }
have()  { command -v "$1" &>/dev/null; }
need_sudo() { ((EUID)) && sudo "$@" || "$@"; }   # führt Befehle mit sudo, falls nötig

# ------- OS‑Erkennung ------------------------------------------------------
OS="$(uname -s)"
case "$OS" in
  Linux)   PKG="apt";;
  Darwin)  PKG="brew";;
  *)       echo "Unsupported OS: $OS"; exit 1;;
esac

# ------- Paketquellen aktualisieren ---------------------------------------
if [[ $PKG == apt ]]; then
  log "apt update"
  need_sudo apt-get update -y
elif [[ $PKG == brew ]]; then
  if ! have brew; then
    log "installiere Homebrew"
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
  fi
  log "brew update"
  brew update
fi

# ------- Basis­pakete ------------------------------------------------------
install_pkg() {
  local name="$1" id="$2"
  if have "$id"; then
    log "$name bereits installiert → $( "$id" --version | head -1 )"
  else
    log "installiere $name"
    if [[ $PKG == apt ]];   then need_sudo apt-get install -y "$id";
    else                          brew install "$id";           fi
  fi
}

install_pkg "Git"     git
install_pkg "Maven"   mvn
install_pkg "OpenJDK" java

# ------- Docker ------------------------------------------------------------
if ! have docker; then
  log "installiere Docker"
  if [[ $PKG == apt ]]; then
    need_sudo apt-get install -y docker.io
    need_sudo systemctl enable --now docker
  else
    brew install --cask docker
    open -a Docker >/dev/null 2>&1 || true
  fi
fi

# Benutzer in Docker‑Gruppe?
if [[ $PKG == apt && ! $(groups "$USER") =~ docker ]]; then
  log "füge $USER der docker‑Gruppe hinzu (Neu­anmeldung nötig)"
  need_sudo usermod -aG docker "$USER"
fi

# ------- IDE‑Auswahl -------------------------------------------------------
ask "IntelliJ (i), VS Code (v) oder Eclipse (e) installieren? [i/v/e/N]:" && IDE=${REPLY,,}

case "$IDE" in
  i*)
    [[ $PKG == brew ]] && brew install --cask intellij-idea-ce \
                      || need_sudo snap install intellij-idea-community --classic
    ;;
  v*)
    [[ $PKG == brew ]] && brew install --cask visual-studio-code \
                      || need_sudo snap install code --classic
    ;;
  e*)
    [[ $PKG == brew ]] && brew install --cask eclipse-java \
                      || need_sudo snap install eclipse --classic
    ;;
  *)
    log "IDE‑Installation übersprungen."
    ;;
esac

log "Fertig! Starte ein neues Terminal und prüfe die Versionen:"
echo "  java --version  | mvn --version  | git --version  | docker --version"
