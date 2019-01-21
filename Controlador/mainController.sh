now="$(date)"
echo "[ mainController ][ mainController.sh Inicio ][ "$now" ]"

./main 5
./main 10
./main 15
./main 20

now="$(date)"
echo "[ mainController ][ mainController.sh Terminado ][ "$now" ]"
