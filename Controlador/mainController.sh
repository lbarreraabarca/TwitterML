now="$(date)"
echo "[ mainController ][ mainController.sh Inicio ][ "$now" ]"

./main.sh 5
./main.sh 10
./main.sh 15
./main.sh 20

now="$(date)"
echo "[ mainController ][ mainController.sh Terminado ][ "$now" ]"
