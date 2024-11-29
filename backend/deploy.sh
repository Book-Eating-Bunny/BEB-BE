cd ${BACKEND_DIR}/backend/build/libs

nohup java -jar backend-0.0.1-SNAPSHOT.jar > /dev/null 2>&1 &

echo "deploy 파일 실행했다"