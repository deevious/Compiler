int factorial(int num) {
	int fact, counter;
	fact = 1;
	while (num > 1) {
		fact = fact * num;
		num = num - 1;
	}
	
	return;
}

void main() {
	int num;
	num = 8;
	num = factorial(num);
	writeln(num);
}