search_dir=/home/runner/work/atcm-crawler/atcm-crawler/ant/jar
for entry in "$search_dir"/*
do
  if [ -f "$entry" ];then
    echo "$entry"
    curl https://nuzal.kr/mngr/file/uploadCrawlingJar -F file=@"$entry"
  fi
done