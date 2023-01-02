#! /bin/sh
# install:
# jq from https://stedolan.github.io/jq/
# gh from https://cli.github.com/manual/tagg
for num in `gh release list 2>/dev/null | awk '{print $1}'`; do
  gh release delete $num -y
done

git tag -d $(git tag -l)
git fetch
# Note: pushing once should be faster than multiple times
git push origin --delete $(git tag -l)
git tag -d $(git tag -l)

