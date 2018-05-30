##################
# Docker section #
##################

# Usage:
# make lein [cmd=]
cmd ?=

lein:
	docker run \
		--rm \
		-it \
		-v "$(PWD)":/app \
		-w /app \
		clojure:alpine lein $(cmd)

clean:
	@make lein cmd='clean'

run:
	@make lein cmd='figwheel dev'

build:
	@make lein cmd='cljsbuild once min'
