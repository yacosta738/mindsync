# Description: Makefile for mindsync-frontend
# Author: Yuniel Acosta <https://twitter.com/yacosta738>

# Path: apps/mindsync-frontend/Makefile (start)
.PHONY: setup
setup:
	@pnpm run setup

.PHONY: update-deps
update-deps:
	@pnpm run update-deps

.PHONY: build
build: deps compile

.PHONY: deps
deps: check-pnpm
	@pnpm install

.PHONY: compile
compile:
	@pnpm run build

.PHONY: start
start:
	@pnpm dev

.PHONY: test
test:
	@pnpm test

.PHONY: lint
lint:
	@pnpm run lint

.PHONY: lint-fix
lint-fix:
	@pnpm run lint:fix

.PHONY: clean
clean:
	@pnpm run clean

check-pnpm:
	@command -v pnpm >/dev/null 2>&1 || ( echo "⚠️ pnpm not installed. Executing npm install -g pnpm to install." && npm install -g pnpm )
