

struct class_std_ClassInfo1997483107;
struct class_std_String1804578194;
void* malloc(unsigned int);
void free(void*);
void print(const char*);
void println(const char*);
void print_s(struct class_std_String1804578194*);
void println_s(struct class_std_String1804578194*);
struct class_std_Object1935960676;

struct class_std_Object1935960676_vtable {
	int offset;
	int (*hashcode261036375) (void*);
	int (*equals1678991472) (void*, struct class_std_Object1935960676*);
	void (*drop115478603) (void*);
	struct class_std_String1804578194* (*toString1664535608) (void*);
};
struct class_std_Object1935960676 {
	struct class_std_Object1935960676_vtable* vtable;
	struct class_std_ClassInfo1997483107* info;
	long int references;
	struct class_std_ClassInfo1997483107* (*getClassInfo92756204) (void*);
};

struct class_std_ClassInfo1997483107* std_Object_getClassInfo1728443284(void*);
int std_Object_hashcode1560163113(void*);
int std_Object_equals142208016(void*, struct class_std_Object1935960676*);
void std_Object_drop1705720885(void*);
struct class_std_String1804578194* std_Object_toString809232200(void*);

struct class_std_Object1935960676* construct_std_Object1_311531241(void*);

static struct class_std_Object1935960676* class_std_Object1935960676_init1924557475() {
    struct class_std_Object1935960676* output;
    output = calloc(1, sizeof(struct class_std_Object1935960676));
    struct class_std_Object1935960676_vtable* vtable;
    vtable = malloc(sizeof(struct class_std_Object1935960676_vtable));
    (*output).vtable = vtable;
    (*vtable).offset = 0;
    (*vtable).hashcode261036375 = std_Object_hashcode1560163113;
    (*vtable).equals1678991472 = std_Object_equals142208016;
    (*vtable).drop115478603 = std_Object_drop1705720885;
    (*vtable).toString1664535608 = std_Object_toString809232200;
    (*output).getClassInfo92756204 = std_Object_getClassInfo1728443284;
    return output;
}

struct class_std_ClassInfo1997483107* std_Object_getClassInfo1728443284(void* __this) {
    struct class_std_Object1935960676* this = __this;
    return (*this).info;
}



int std_Object_equals142208016(void* __this, struct class_std_Object1935960676* other) {
    struct class_std_Object1935960676* this = __this;
    return ((*this).vtable->hashcode261036375(this)==(*other).vtable->hashcode261036375(other));
}


void std_Object_drop1705720885(void* __this) {
    struct class_std_Object1935960676* this = __this;
    free(this);
}


struct class_std_ClassInfo1997483107;

struct class_std_ClassInfo1997483107_vtable {
	int offset;
	int (*hashcode261036375) (void*);
	int (*equals1678991472) (void*, struct class_std_Object1935960676*);
	void (*drop115478603) (void*);
	struct class_std_String1804578194* (*toString1664535608) (void*);
};
struct class_std_ClassInfo1997483107 {
	struct class_std_ClassInfo1997483107_vtable* vtable;
	struct class_std_ClassInfo1997483107* info;
	long int references;
	struct class_std_ClassInfo1997483107* (*getClassInfo92756204) (void*);
	struct class_std_String1804578194* name;
	struct class_std_ClassInfo1997483107* parent;
	int classHash;
	struct class_std_String1804578194* (*getName37078109) (void*);
};

struct class_std_String1804578194* std_ClassInfo_getName467971907(void*);
int std_Object_hashcode1560163113(void*);
int std_Object_equals142208016(void*, struct class_std_Object1935960676*);
void std_Object_drop1705720885(void*);
struct class_std_String1804578194* std_Object_toString809232200(void*);

struct class_std_ClassInfo1997483107* construct_std_ClassInfo1604335129_1910026682(void*, struct class_std_String1804578194*, struct class_std_ClassInfo1997483107*, int);

static struct class_std_ClassInfo1997483107* class_std_ClassInfo1997483107_init158675559() {
    struct class_std_ClassInfo1997483107* output;
    output = calloc(1, sizeof(struct class_std_ClassInfo1997483107));
    struct class_std_ClassInfo1997483107_vtable* vtable;
    vtable = malloc(sizeof(struct class_std_ClassInfo1997483107_vtable));
    (*output).vtable = vtable;
    (*vtable).offset = 0;
    (*vtable).hashcode261036375 = std_Object_hashcode1560163113;
    (*vtable).equals1678991472 = std_Object_equals142208016;
    (*vtable).drop115478603 = std_Object_drop1705720885;
    (*vtable).toString1664535608 = std_Object_toString809232200;
    (*output).getClassInfo92756204 = std_Object_getClassInfo1728443284;
    (*output).getName37078109 = std_ClassInfo_getName467971907;
    return output;
}

struct class_std_String1804578194* std_ClassInfo_getName467971907(void* __this) {
    struct class_std_ClassInfo1997483107* this = __this;
    struct class_std_Object1935960676* super = __this;
    return (*this).name;
}



struct class_std_String1804578194;

struct class_std_String1804578194_vtable {
	int offset;
	int (*hashcode261036375) (void*);
	int (*equals1678991472) (void*, struct class_std_Object1935960676*);
	void (*drop115478603) (void*);
	struct class_std_String1804578194* (*toString1664535608) (void*);
};
struct class_std_String1804578194 {
	struct class_std_String1804578194_vtable* vtable;
	struct class_std_ClassInfo1997483107* info;
	long int references;
	struct class_std_ClassInfo1997483107* (*getClassInfo92756204) (void*);
	char* backingPtr;
	int length;
	struct class_std_String1804578194* (*concat957070914) (void*, struct class_std_String1804578194*);
	struct class_std_String1804578194* (*concat1663899886) (void*, char*);
	const char* (*getCStr36737184) (void*);
};

struct class_std_String1804578194* std_String_concat1425969022(void*, struct class_std_String1804578194*);
struct class_std_String1804578194* std_String_concat719140050(void*, char*);
const char* std_String_getCStr1875190176(void*);
int std_Object_hashcode1560163113(void*);
int std_Object_equals142208016(void*, struct class_std_Object1935960676*);
void std_String_drop1796448757(void*);
struct class_std_String1804578194* std_Object_toString809232200(void*);

struct class_std_String1804578194* construct_std_String1675763803_6032799(void*, const char*);
struct class_std_String1804578194* construct_std_String1_2147427341(void*);

static struct class_std_String1804578194* class_std_String1804578194_init2018237142() {
    struct class_std_String1804578194* output;
    output = calloc(1, sizeof(struct class_std_String1804578194));
    struct class_std_String1804578194_vtable* vtable;
    vtable = malloc(sizeof(struct class_std_String1804578194_vtable));
    (*output).vtable = vtable;
    (*vtable).offset = 0;
    (*vtable).hashcode261036375 = std_Object_hashcode1560163113;
    (*vtable).equals1678991472 = std_Object_equals142208016;
    (*vtable).drop115478603 = std_String_drop1796448757;
    (*vtable).toString1664535608 = std_Object_toString809232200;
    (*output).getClassInfo92756204 = std_Object_getClassInfo1728443284;
    (*output).concat957070914 = std_String_concat1425969022;
    (*output).concat1663899886 = std_String_concat719140050;
    (*output).getCStr36737184 = std_String_getCStr1875190176;
    return output;
}

struct class_std_String1804578194* std_String_concat1425969022(void* __this, struct class_std_String1804578194* other) {
    struct class_std_String1804578194* this = __this;
    struct class_std_Object1935960676* super = __this;
    char* next = (char*) malloc((sizeof(char)*(((*this).length+(*other).length)+1)));
    for (int i = 0;(i<(*this).length); i++){
        next[i] = (char) (*this).backingPtr[i];
    }



    for (int i = 0;(i<(*other).length); i++){
        next[(i+(*this).length)] = (char) (*other).backingPtr[i];
    }



    next[((*this).length+(*other).length)] = '\0';
    struct class_std_String1804578194* output = construct_std_String1675763803_6032799(class_std_String1804578194_init2018237142(), next);
    free(next);
    return output;
}


struct class_std_String1804578194* std_String_concat719140050(void* __this, char* other) {
    struct class_std_String1804578194* this = __this;
    struct class_std_Object1935960676* super = __this;
    return (*this).concat957070914(this, construct_std_String1675763803_6032799(class_std_String1804578194_init2018237142(), other));
}


const char* std_String_getCStr1875190176(void* __this) {
    struct class_std_String1804578194* this = __this;
    struct class_std_Object1935960676* super = __this;
    return (*this).backingPtr;
}




struct class_std_String1804578194* construct_std_String1675763803_6032799(void* __this, const char* bp) {
    struct class_std_String1804578194* this = (struct class_std_String1804578194*) __this;
    const char* ptr = bp;
    for (;((*ptr)!='\0'); ptr++){
        ++(*this).length;
    }


    (*this).backingPtr = malloc((sizeof(char)*((*this).length+1)));
    (*this).backingPtr[(*this).length] = '\0';
    for (int i = 0;(i<(*this).length); i++){
        (*this).backingPtr[i] = (char) bp[i];
    }
    return this;
}


struct class_std_String1804578194* construct_std_String1_2147427341(void* __this) {
    construct_std_String1675763803_6032799(__this, "");
    struct class_std_String1804578194* this = (struct class_std_String1804578194*) __this;
    return this;
}

static void super_std_Object_drop17057208851863457476(void* __this) {
    struct class_std_String1804578194* this = __this;
    struct class_std_Object1935960676* super = __this;
    void (*old) (void*);
    old = (*(*this).vtable).drop115478603;
    (*(*this).vtable).drop115478603 = std_Object_drop1705720885;
    (*(*this).vtable).drop115478603(__this);
    (*(*this).vtable).drop115478603 = old;
}

void std_String_drop1796448757(void* __this) {
    struct class_std_String1804578194* this = __this;
    struct class_std_Object1935960676* super = __this;
    free((*this).backingPtr);
    super_std_Object_drop17057208851863457476(super);
}


void print_s(struct class_std_String1804578194* o) {
    print((*o).getCStr36737184(o));
}

void println_s(struct class_std_String1804578194* o) {
    println((*o).getCStr36737184(o));
}

void _main(int, struct class_std_String1804578194*[]);
struct class_std_String1804578194* std_Object_toString809232200(void* __this) {
    struct class_std_Object1935960676* this = __this;
    return (*(*this).info).getName37078109(this);
}

int std_Object_hashcode1560163113(void* __this) {
    struct class_std_Object1935960676* this = __this;
    return (int) this;
}

int main(int argc, char* argv[]) {
    struct class_std_String1804578194* args[(argc-1)];
    for (int i = 1;(i<argc); ++i){
        args[(i-1)] = construct_std_String1675763803_6032799(class_std_String1804578194_init2018237142(), argv[i]);
    }


    _main((argc-1), args);
    for (int i = 1;(i<argc); ++i){
        (*args[(i-1)]).vtable->drop115478603(args[(i-1)]);
    }


    return 0;
}

void _main(int argc, struct class_std_String1804578194* args[]) {
    struct class_std_String1804578194* total = construct_std_String1_2147427341(class_std_String1804578194_init2018237142());
    int first = 1;
    for (int i = 0;(i<argc); ++i){
        if (first) first = 0; else total = (*total).concat1663899886(total, ", ");

        total = (*total).concat957070914(total, args[i]);
    }




    println_s(total);
}

