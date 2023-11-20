# builds and runs this app in docker (zero install)
runDocker: buildDocker
	source ./build.sh && runDocker
# builds and runs this app locally 
run:
	source ./build.sh && run
# builds this app in docker (zero install)	
buildDocker: 
	source ./build.sh && buildDocker
# builds and pushs to dockerhub
push: buildDocker
	source ./build.sh && push
# installs this app using argo, pointing at the current branch (e.g. master, dev, whatever git branch you're currently on)
installArgo:
	source ./build.sh && installArgo
uninstallArgo:
	source ./build.sh && uninstallArgo
test:
	source ./build.sh && test
buildLocally:
	. ./build.sh && buildLocally