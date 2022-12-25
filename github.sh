#! bash
# publish results to GitHub reeases
# args: version artifact
echo "=========github.sh========="
gh release create $1
gh release upload $1 $2 --clobber -R dicklieber/rotatorcontrol"
