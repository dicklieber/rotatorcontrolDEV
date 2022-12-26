#! bash
# publish results to GitHub reeases
# args: version artifact
echo "=========github.sh  $1 $2 ========="
gh release create $1 --generate-notes
gh release upload $1 $2 --clobber -R dicklieber/rotatorcontrol
