

struct class_std_ClassInfo1072900803;
struct class_std_String1565806798;
typedef unsigned long int class_id;
void* malloc(unsigned int);
void* calloc(unsigned int, unsigned int);
void free(void*);
void print(const char*);
void println(const char*);
void print_s(struct class_std_String1565806798*);
void println_s(struct class_std_String1565806798*);
struct class_std_ClassInfo1072900803* getClass(class_id);
struct class_std_Object1434424316;

struct class_std_Object1434424316_vtable {
	int offset;
	int (*hashcode261036375) (void*);
	int (*equals665279894) (void*, struct class_std_Object1434424316*);
	void (*drop115478603) (void*);
	struct class_std_String1565806798* (*toString1664535608) (void*);
};
struct class_std_Object1434424316 {
	struct class_std_Object1434424316_vtable* vtable;
	struct class_std_ClassInfo1072900803* info;
	long int references;
	struct class_std_ClassInfo1072900803* (*getClass2062954782) (void*);
};

struct class_std_ClassInfo1072900803* std_Object_getClass1590701935(void*);
int std_Object_hashcode902346954(void*);
int std_Object_equals1306590473(void*, struct class_std_Object1434424316*);
void std_Object_drop756789182(void*);
struct class_std_String1565806798* std_Object_toString1023225029(void*);

struct class_std_Object1434424316* construct_std_Object1_1371321660(void*);

static struct class_std_Object1434424316* class_std_Object1434424316_init1258967178() {
    struct class_std_Object1434424316* output;
    output = calloc(1, sizeof(struct class_std_Object1434424316));
    struct class_std_Object1434424316_vtable* vtable;
    vtable = malloc(sizeof(struct class_std_Object1434424316_vtable));
    (*output).vtable = vtable;
    (*vtable).offset = 0;
    (*vtable).hashcode261036375 = std_Object_hashcode902346954;
    (*vtable).equals665279894 = std_Object_equals1306590473;
    (*vtable).drop115478603 = std_Object_drop756789182;
    (*vtable).toString1664535608 = std_Object_toString1023225029;
    (*output).getClass2062954782 = std_Object_getClass1590701935;
    (*output).info = (struct class_std_ClassInfo1072900803*) {0};
    (*output).references = (long int) {0};
    return output;
}



struct class_std_ClassInfo1072900803;

struct class_std_ClassInfo1072900803_vtable {
	int offset;
	int (*hashcode261036375) (void*);
	int (*equals665279894) (void*, struct class_std_Object1434424316*);
	void (*drop115478603) (void*);
	struct class_std_String1565806798* (*toString1664535608) (void*);
	int (*equals1298758559) (void*, struct class_std_ClassInfo1072900803*);
};
struct class_std_ClassInfo1072900803 {
	struct class_std_ClassInfo1072900803_vtable* vtable;
	struct class_std_ClassInfo1072900803* info;
	long int references;
	struct class_std_ClassInfo1072900803* (*getClass2062954782) (void*);
	struct class_std_String1565806798* name;
	struct class_std_ClassInfo1072900803* parent;
	int classHash;
	struct class_std_String1565806798* (*getName37078109) (void*);
	int (*is1960766209) (void*, struct class_std_Object1434424316*);
};

struct class_std_String1565806798* std_ClassInfo_getName1994538160(void*);
int std_ClassInfo_is376741036(void*, struct class_std_Object1434424316*);
int std_Object_hashcode902346954(void*);
int std_ClassInfo_equals1672227351(void*, struct class_std_Object1434424316*);
void std_Object_drop756789182(void*);
struct class_std_String1565806798* std_Object_toString1023225029(void*);
int std_ClassInfo_equals658701492(void*, struct class_std_ClassInfo1072900803*);

struct class_std_ClassInfo1072900803* construct_std_ClassInfo556057004_873147240(void*, struct class_std_String1565806798*, struct class_std_ClassInfo1072900803*, int);

static struct class_std_ClassInfo1072900803* class_std_ClassInfo1072900803_init767881601() {
    struct class_std_ClassInfo1072900803* output;
    output = calloc(1, sizeof(struct class_std_ClassInfo1072900803));
    struct class_std_ClassInfo1072900803_vtable* vtable;
    vtable = malloc(sizeof(struct class_std_ClassInfo1072900803_vtable));
    (*output).vtable = vtable;
    (*vtable).offset = 0;
    (*vtable).hashcode261036375 = std_Object_hashcode902346954;
    (*vtable).equals665279894 = std_ClassInfo_equals1672227351;
    (*vtable).drop115478603 = std_Object_drop756789182;
    (*vtable).toString1664535608 = std_Object_toString1023225029;
    (*vtable).equals1298758559 = std_ClassInfo_equals658701492;
    (*output).getClass2062954782 = std_Object_getClass1590701935;
    (*output).getName37078109 = std_ClassInfo_getName1994538160;
    (*output).is1960766209 = std_ClassInfo_is376741036;
    (*output).info = output;
    (*output).references = (long int) {0};
    (*output).name = (struct class_std_String1565806798*) {0};
    (*output).parent = (struct class_std_ClassInfo1072900803*) {0};
    (*output).classHash = (int) {0};
    return output;
}




static int super_std_Object_equals1306590473343211960(void* __this, struct class_std_Object1434424316* other) {
    struct class_std_ClassInfo1072900803* this = __this;
    struct class_std_Object1434424316* super = __this;
    int (*old) (void*, struct class_std_Object1434424316*);
    old = (*(*this).vtable).equals665279894;
    (*(*this).vtable).equals665279894 = std_Object_equals1306590473;
    int output;
    output = (*(*this).vtable).equals665279894(__this, other);
    (*(*this).vtable).equals665279894 = old;
    return output;
}

struct class_std_String1565806798;

struct class_std_String1565806798_vtable {
	int offset;
	int (*hashcode261036375) (void*);
	int (*equals665279894) (void*, struct class_std_Object1434424316*);
	void (*drop115478603) (void*);
	struct class_std_String1565806798* (*toString1664535608) (void*);
};
struct class_std_String1565806798 {
	struct class_std_String1565806798_vtable* vtable;
	struct class_std_ClassInfo1072900803* info;
	long int references;
	struct class_std_ClassInfo1072900803* (*getClass2062954782) (void*);
	char* backingPtr;
	int length;
	struct class_std_String1565806798* (*concat1580926564) (void*, struct class_std_String1565806798*);
	struct class_std_String1565806798* (*concat1663899886) (void*, char*);
	const char* (*getCStr36737184) (void*);
};

struct class_std_String1565806798* std_String_concat2131509271(void*, struct class_std_String1565806798*);
struct class_std_String1565806798* std_String_concat1113317179(void*, char*);
const char* std_String_getCStr587319891(void*);
int std_Object_hashcode902346954(void*);
int std_Object_equals1306590473(void*, struct class_std_Object1434424316*);
void std_String_drop666061310(void*);
struct class_std_String1565806798* std_Object_toString1023225029(void*);

struct class_std_String1565806798* construct_std_String1556956129_233001861(void*, const char*);
struct class_std_String1565806798* construct_std_String1_1087749536(void*);

static struct class_std_String1565806798* class_std_String1565806798_init1174076527() {
    struct class_std_String1565806798* output;
    output = calloc(1, sizeof(struct class_std_String1565806798));
    struct class_std_String1565806798_vtable* vtable;
    vtable = malloc(sizeof(struct class_std_String1565806798_vtable));
    (*output).vtable = vtable;
    (*vtable).offset = 0;
    (*vtable).hashcode261036375 = std_Object_hashcode902346954;
    (*vtable).equals665279894 = std_Object_equals1306590473;
    (*vtable).drop115478603 = std_String_drop666061310;
    (*vtable).toString1664535608 = std_Object_toString1023225029;
    (*output).getClass2062954782 = std_Object_getClass1590701935;
    (*output).concat1580926564 = std_String_concat2131509271;
    (*output).concat1663899886 = std_String_concat1113317179;
    (*output).getCStr36737184 = std_String_getCStr587319891;
    (*output).info = (struct class_std_ClassInfo1072900803*) {0};
    (*output).references = (long int) {0};
    (*output).backingPtr = (char*) {0};
    (*output).length = (int) {0};
    return output;
}





static void super_std_Object_drop756789182233676185(void* __this) {
    struct class_std_String1565806798* this = __this;
    struct class_std_Object1434424316* super = __this;
    void (*old) (void*);
    old = (*(*this).vtable).drop115478603;
    (*(*this).vtable).drop115478603 = std_Object_drop756789182;
    (*(*this).vtable).drop115478603(__this);
    (*(*this).vtable).drop115478603 = old;
}

struct class_animals_animal1162058053;

struct class_animals_animal1162058053_vtable {
	int offset;
	int (*hashcode261036375) (void*);
	int (*equals665279894) (void*, struct class_std_Object1434424316*);
	void (*drop115478603) (void*);
	struct class_std_String1565806798* (*toString1664535608) (void*);
	void (*says115909444) (void*);
};
struct class_animals_animal1162058053 {
	struct class_animals_animal1162058053_vtable* vtable;
	struct class_std_ClassInfo1072900803* info;
	long int references;
	struct class_std_ClassInfo1072900803* (*getClass2062954782) (void*);
	char* species;
	int numberOfLegs;
	int (*getNumberOfLegs1629143273) (void*);
};

int animals_animal_getNumberOfLegs1113618518(void*);
int std_Object_hashcode902346954(void*);
int std_Object_equals1306590473(void*, struct class_std_Object1434424316*);
void std_Object_drop756789182(void*);
struct class_std_String1565806798* std_Object_toString1023225029(void*);
void animals_animal_says631434199(void*);

struct class_animals_animal1162058053* construct_animals_animal1860336591_1254192836(void*, char*, int);

static struct class_animals_animal1162058053* class_animals_animal1162058053_init1416492964() {
    struct class_animals_animal1162058053* output;
    output = calloc(1, sizeof(struct class_animals_animal1162058053));
    struct class_animals_animal1162058053_vtable* vtable;
    vtable = malloc(sizeof(struct class_animals_animal1162058053_vtable));
    (*output).vtable = vtable;
    (*vtable).offset = 0;
    (*vtable).hashcode261036375 = std_Object_hashcode902346954;
    (*vtable).equals665279894 = std_Object_equals1306590473;
    (*vtable).drop115478603 = std_Object_drop756789182;
    (*vtable).toString1664535608 = std_Object_toString1023225029;
    (*vtable).says115909444 = animals_animal_says631434199;
    (*output).getClass2062954782 = std_Object_getClass1590701935;
    (*output).getNumberOfLegs1629143273 = animals_animal_getNumberOfLegs1113618518;
    (*output).info = (struct class_std_ClassInfo1072900803*) {0};
    (*output).references = (long int) {0};
    (*output).species = (char*) {0};
    (*output).numberOfLegs = (int) {0};
    return output;
}

int animals_animal_getNumberOfLegs1113618518(void* __this) {
    struct class_animals_animal1162058053* this = __this;
    struct class_std_Object1434424316* super = __this;
    return (*this).numberOfLegs;
}




struct class_animals_animal1162058053* construct_animals_animal1860336591_1254192836(void* __this, char* species, int numberOfLegs) {
    struct class_animals_animal1162058053* this = (struct class_animals_animal1162058053*) __this;
    (*this).species = species;
    (*this).numberOfLegs = numberOfLegs;
    return this;
}

void animals_animal_says631434199(void* __this) {
    struct class_animals_animal1162058053* this = __this;
    struct class_std_Object1434424316* super = __this;
    print((*this).species);
    print(" says ");
}


struct class_animals_quadAnimal1399106196;

struct class_animals_quadAnimal1399106196_vtable {
	int offset;
	int (*hashcode261036375) (void*);
	int (*equals665279894) (void*, struct class_std_Object1434424316*);
	void (*drop115478603) (void*);
	struct class_std_String1565806798* (*toString1664535608) (void*);
	void (*says115909444) (void*);
};
struct class_animals_quadAnimal1399106196 {
	struct class_animals_quadAnimal1399106196_vtable* vtable;
	struct class_std_ClassInfo1072900803* info;
	long int references;
	struct class_std_ClassInfo1072900803* (*getClass2062954782) (void*);
	char* species;
	int numberOfLegs;
	int (*getNumberOfLegs1629143273) (void*);
};

int std_Object_hashcode902346954(void*);
int std_Object_equals1306590473(void*, struct class_std_Object1434424316*);
void std_Object_drop756789182(void*);
struct class_std_String1565806798* std_Object_toString1023225029(void*);
void animals_quadAnimal_says278556855(void*);

struct class_animals_quadAnimal1399106196* construct_animals_quadAnimal285377382_2110536518(void*, char*);

static struct class_animals_quadAnimal1399106196* class_animals_quadAnimal1399106196_init427816941() {
    struct class_animals_quadAnimal1399106196* output;
    output = calloc(1, sizeof(struct class_animals_quadAnimal1399106196));
    struct class_animals_quadAnimal1399106196_vtable* vtable;
    vtable = malloc(sizeof(struct class_animals_quadAnimal1399106196_vtable));
    (*output).vtable = vtable;
    (*vtable).offset = 0;
    (*vtable).hashcode261036375 = std_Object_hashcode902346954;
    (*vtable).equals665279894 = std_Object_equals1306590473;
    (*vtable).drop115478603 = std_Object_drop756789182;
    (*vtable).toString1664535608 = std_Object_toString1023225029;
    (*vtable).says115909444 = animals_quadAnimal_says278556855;
    (*output).getClass2062954782 = std_Object_getClass1590701935;
    (*output).getNumberOfLegs1629143273 = animals_animal_getNumberOfLegs1113618518;
    (*output).info = (struct class_std_ClassInfo1072900803*) {0};
    (*output).references = (long int) {0};
    (*output).species = (char*) {0};
    (*output).numberOfLegs = (int) {0};
    return output;
}



struct class_animals_quadAnimal1399106196* construct_animals_quadAnimal285377382_2110536518(void* __this, char* name) {
    construct_animals_animal1860336591_1254192836(__this, name, 4);
    struct class_animals_quadAnimal1399106196* this = (struct class_animals_quadAnimal1399106196*) __this;
    return this;
}

static void super_animals_animal_says6314341991444806604(void* __this) {
    struct class_animals_quadAnimal1399106196* this = __this;
    struct class_animals_animal1162058053* super = __this;
    void (*old) (void*);
    old = (*(*this).vtable).says115909444;
    (*(*this).vtable).says115909444 = animals_animal_says631434199;
    (*(*this).vtable).says115909444(__this);
    (*(*this).vtable).says115909444 = old;
}

void animals_quadAnimal_says278556855(void* __this) {
    struct class_animals_quadAnimal1399106196* this = __this;
    struct class_animals_animal1162058053* super = __this;
    super_animals_animal_says6314341991444806604(super);
    print("I have 4 legs!");
}


struct class_animals_domesticated127460729;

struct class_animals_domesticated127460729_vtable {
	int offset;
	int (*hashcode261036375) (void*);
	int (*equals665279894) (void*, struct class_std_Object1434424316*);
	void (*drop115478603) (void*);
	struct class_std_String1565806798* (*toString1664535608) (void*);
	void (*says115909444) (void*);
};
struct class_animals_domesticated127460729 {
	struct class_animals_domesticated127460729_vtable* vtable;
	struct class_std_ClassInfo1072900803* info;
	long int references;
	struct class_std_ClassInfo1072900803* (*getClass2062954782) (void*);
	char* species;
	int numberOfLegs;
	int (*getNumberOfLegs1629143273) (void*);
	char* name;
	char* (*getName37078109) (void*);
};

char* animals_domesticated_getName1805226864(void*);
int std_Object_hashcode902346954(void*);
int std_Object_equals1306590473(void*, struct class_std_Object1434424316*);
void std_Object_drop756789182(void*);
struct class_std_String1565806798* std_Object_toString1023225029(void*);
void animals_domesticated_says1884058199(void*);

struct class_animals_domesticated127460729* construct_animals_domesticated1644066936_1237274774(void*, char*, char*);

static struct class_animals_domesticated127460729* class_animals_domesticated127460729_init1266940201() {
    struct class_animals_domesticated127460729* output;
    output = calloc(1, sizeof(struct class_animals_domesticated127460729));
    struct class_animals_domesticated127460729_vtable* vtable;
    vtable = malloc(sizeof(struct class_animals_domesticated127460729_vtable));
    (*output).vtable = vtable;
    (*vtable).offset = 0;
    (*vtable).hashcode261036375 = std_Object_hashcode902346954;
    (*vtable).equals665279894 = std_Object_equals1306590473;
    (*vtable).drop115478603 = std_Object_drop756789182;
    (*vtable).toString1664535608 = std_Object_toString1023225029;
    (*vtable).says115909444 = animals_domesticated_says1884058199;
    (*output).getClass2062954782 = std_Object_getClass1590701935;
    (*output).getNumberOfLegs1629143273 = animals_animal_getNumberOfLegs1113618518;
    (*output).getName37078109 = animals_domesticated_getName1805226864;
    (*output).info = (struct class_std_ClassInfo1072900803*) {0};
    (*output).references = (long int) {0};
    (*output).species = (char*) {0};
    (*output).numberOfLegs = (int) {0};
    (*output).name = (char*) {0};
    return output;
}

char* animals_domesticated_getName1805226864(void* __this) {
    struct class_animals_domesticated127460729* this = __this;
    struct class_animals_quadAnimal1399106196* super = __this;
    return (*this).name;
}




struct class_animals_domesticated127460729* construct_animals_domesticated1644066936_1237274774(void* __this, char* name, char* species) {
    construct_animals_quadAnimal285377382_2110536518(__this, species);
    struct class_animals_domesticated127460729* this = (struct class_animals_domesticated127460729*) __this;
    (*this).name = name;
    return this;
}

static void super_animals_quadAnimal_says278556855487552549(void* __this) {
    struct class_animals_domesticated127460729* this = __this;
    struct class_animals_quadAnimal1399106196* super = __this;
    void (*old) (void*);
    old = (*(*this).vtable).says115909444;
    (*(*this).vtable).says115909444 = animals_quadAnimal_says278556855;
    (*(*this).vtable).says115909444(__this);
    (*(*this).vtable).says115909444 = old;
}

void animals_domesticated_says1884058199(void* __this) {
    struct class_animals_domesticated127460729* this = __this;
    struct class_animals_quadAnimal1399106196* super = __this;
    print((*this).getName37078109(this));
    print(" the ");
    super_animals_quadAnimal_says278556855487552549(super);
    print(", also ARF");
}


struct class_animals_dog1719693179;

struct class_animals_dog1719693179_vtable {
	int offset;
	int (*hashcode261036375) (void*);
	int (*equals665279894) (void*, struct class_std_Object1434424316*);
	void (*drop115478603) (void*);
	struct class_std_String1565806798* (*toString1664535608) (void*);
	void (*says115909444) (void*);
};
struct class_animals_dog1719693179 {
	struct class_animals_dog1719693179_vtable* vtable;
	struct class_std_ClassInfo1072900803* info;
	long int references;
	struct class_std_ClassInfo1072900803* (*getClass2062954782) (void*);
	char* species;
	int numberOfLegs;
	int (*getNumberOfLegs1629143273) (void*);
	char* name;
	char* (*getName37078109) (void*);
};

int std_Object_hashcode902346954(void*);
int std_Object_equals1306590473(void*, struct class_std_Object1434424316*);
void std_Object_drop756789182(void*);
struct class_std_String1565806798* std_Object_toString1023225029(void*);
void animals_domesticated_says1884058199(void*);

struct class_animals_dog1719693179* construct_animals_dog716083631_1094823772(void*, char*);
struct class_animals_dog1719693179* construct_animals_dog1_507047854(void*);

static struct class_animals_dog1719693179* class_animals_dog1719693179_init231346112() {
    struct class_animals_dog1719693179* output;
    output = calloc(1, sizeof(struct class_animals_dog1719693179));
    struct class_animals_dog1719693179_vtable* vtable;
    vtable = malloc(sizeof(struct class_animals_dog1719693179_vtable));
    (*output).vtable = vtable;
    (*vtable).offset = 0;
    (*vtable).hashcode261036375 = std_Object_hashcode902346954;
    (*vtable).equals665279894 = std_Object_equals1306590473;
    (*vtable).drop115478603 = std_Object_drop756789182;
    (*vtable).toString1664535608 = std_Object_toString1023225029;
    (*vtable).says115909444 = animals_domesticated_says1884058199;
    (*output).getClass2062954782 = std_Object_getClass1590701935;
    (*output).getNumberOfLegs1629143273 = animals_animal_getNumberOfLegs1113618518;
    (*output).getName37078109 = animals_domesticated_getName1805226864;
    (*output).info = (struct class_std_ClassInfo1072900803*) {0};
    (*output).references = (long int) {0};
    (*output).species = (char*) {0};
    (*output).numberOfLegs = (int) {0};
    (*output).name = (char*) {0};
    return output;
}



struct class_animals_dog1719693179* construct_animals_dog716083631_1094823772(void* __this, char* name) {
    construct_animals_domesticated1644066936_1237274774(__this, name, "dog");
    struct class_animals_dog1719693179* this = (struct class_animals_dog1719693179*) __this;
    return this;
}


struct class_animals_dog1719693179* construct_animals_dog1_507047854(void* __this) {
    construct_animals_dog716083631_1094823772(__this, "unknown");
    struct class_animals_dog1719693179* this = (struct class_animals_dog1719693179*) __this;
    return this;
}

struct class_cat1984470985;

struct class_cat1984470985_vtable {
	int offset;
	int (*hashcode261036375) (void*);
	int (*equals665279894) (void*, struct class_std_Object1434424316*);
	void (*drop115478603) (void*);
	struct class_std_String1565806798* (*toString1664535608) (void*);
	void (*says115909444) (void*);
};
struct class_cat1984470985 {
	struct class_cat1984470985_vtable* vtable;
	struct class_std_ClassInfo1072900803* info;
	long int references;
	struct class_std_ClassInfo1072900803* (*getClass2062954782) (void*);
	char* species;
	int numberOfLegs;
	int (*getNumberOfLegs1629143273) (void*);
	char* name;
	char* (*getName37078109) (void*);
};

int std_Object_hashcode902346954(void*);
int std_Object_equals1306590473(void*, struct class_std_Object1434424316*);
void std_Object_drop756789182(void*);
struct class_std_String1565806798* std_Object_toString1023225029(void*);
void cat_says1178844247(void*);

struct class_cat1984470985* construct_cat791885656_1487619626(void*, char*);

static struct class_cat1984470985* class_cat1984470985_init549573886() {
    struct class_cat1984470985* output;
    output = calloc(1, sizeof(struct class_cat1984470985));
    struct class_cat1984470985_vtable* vtable;
    vtable = malloc(sizeof(struct class_cat1984470985_vtable));
    (*output).vtable = vtable;
    (*vtable).offset = 0;
    (*vtable).hashcode261036375 = std_Object_hashcode902346954;
    (*vtable).equals665279894 = std_Object_equals1306590473;
    (*vtable).drop115478603 = std_Object_drop756789182;
    (*vtable).toString1664535608 = std_Object_toString1023225029;
    (*vtable).says115909444 = cat_says1178844247;
    (*output).getClass2062954782 = std_Object_getClass1590701935;
    (*output).getNumberOfLegs1629143273 = animals_animal_getNumberOfLegs1113618518;
    (*output).getName37078109 = animals_domesticated_getName1805226864;
    (*output).info = (struct class_std_ClassInfo1072900803*) {0};
    (*output).references = (long int) {0};
    (*output).species = (char*) {0};
    (*output).numberOfLegs = (int) {0};
    (*output).name = (char*) {0};
    return output;
}



struct class_cat1984470985* construct_cat791885656_1487619626(void* __this, char* name) {
    construct_animals_domesticated1644066936_1237274774(__this, name, "cat");
    struct class_cat1984470985* this = (struct class_cat1984470985*) __this;
    return this;
}

static void super_animals_domesticated_says18840581991329863068(void* __this) {
    struct class_cat1984470985* this = __this;
    struct class_animals_domesticated127460729* super = __this;
    void (*old) (void*);
    old = (*(*this).vtable).says115909444;
    (*(*this).vtable).says115909444 = animals_domesticated_says1884058199;
    (*(*this).vtable).says115909444(__this);
    (*(*this).vtable).says115909444 = old;
}

void cat_says1178844247(void* __this) {
    struct class_cat1984470985* this = __this;
    struct class_animals_domesticated127460729* super = __this;
    println("I'm a cat, shove off");
}


