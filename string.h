void* malloc(unsigned long i);
char *strcpy(char *dest, const char *src);
char *strcat(char *dest, const char *src);

typedef unsigned long size_t;
size_t strlen(const char* str);

class string {
	char* ptr;
	private int length;

	public string() : this("") {}

	public string(const char* cstr) {
		size_t len = strlen(cstr);
		this->length = len;
		this->ptr = malloc(sizeof(char) * (len + 1));
		this->ptr[len - 1] = '\0';
		strcpy(this->ptr, cstr);
	}

	public string* concat(string other) {
		char str[this->length + other->length + 1];
		strcpy(str, this->ptr);
		strcat(str, other->ptr);
		return new string((char*) str);
	}

	public string* concat(string* other) {
		return this->concat(*other);
	}

	virtual public int get_num_characters() {
		return this->length;
	}
};

class string2 : string {

	public string2(string other) : super(other.ptr) { }

	virtual public int get_num_characters() {
    		return super->get_num_characters() * 2;
    }
};




int main() {

	string* s = new string("hello world");

	string* d = new string("yeet");

	d = d->concat(s);
	int j = (int) 'c';

	int i = d->get_num_characters();

	return 0;
}

