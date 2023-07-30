# Description: Makefile for MindSync project
# Author: Yuniel Acosta <https://twitter.com/yacosta738>

MAKEFILES += $(shell find . -name Makefile)

.PHONY: all
all: check build

.PHONY: check
check:
	@./gradlew clean check sonar jacocoTestReport aggregateReports --warning-mode all

.PHONY: up
up:
	@docker-compose -f infra/docker-compose.yml up -d

.PHONY: down
down:
	@docker-compose infra/docker-compose.yml down

.PHONY: build
build:
	@./gradlew build --warning-mode all

.PHONY: test
test:
	@./gradlew test --warning-mode all

# Start the application
.PHONY: start-frontend-app
start-frontend-app:
	@cd apps/mindsync-frontend && make start

.PHONY: start-backend-app
start-backend-app:
	@./gradlew bootRun --warning-mode all
