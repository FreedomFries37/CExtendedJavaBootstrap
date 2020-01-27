struct class_std_ClassInfo790192477;
struct class_std_String297286482;
typedef unsigned long int class_id;
void* malloc(unsigned int);
void* calloc(unsigned int, unsigned int);
void free(void*);
void print(const char*);
void println(const char*);
void print_s(struct class_std_String297286482*);
void println_s(struct class_std_String297286482*);
struct class_std_ClassInfo790192477* getClass(class_id);
struct class_std_Object428668964;

struct class_std_Object428668964_vtable {
	int offset;
	int (*hashcode261036375) (void*);
	int (*equals2092643753) (void*, struct class_std_Object428668964*);
	void (*drop115478603) (void*);
	struct class_std_String297286482* (*toString1664535608) (void*);
};
struct class_std_Object428668964 {
	struct class_std_Object428668964_vtable* vtable;
	struct class_std_ClassInfo790192477* info;
	long int references;
	struct class_std_ClassInfo790192477* (*getClass2062954782) (void*);
};

struct class_std_ClassInfo790192477* std_Object_getClass2077890512(void*);
int std_Object_hashcode415158377(void*);
int std_Object_equals2048201541(void*, struct class_std_Object428668964*);
void std_Object_drop269600605(void*);
struct class_std_String297286482* std_Object_toString1510413606(void*);

struct class_std_Object428668964* construct_std_Object1_1489517381(void*);

static struct class_std_Object428668964* class_std_Object428668964_init1808783078() {
    struct class_std_Object428668964* output;
    output = calloc(1, sizeof(struct class_std_Object428668964));
    struct class_std_Object428668964_vtable* vtable;
    vtable = malloc(sizeof(struct class_std_Object428668964_vtable));
    (*output).vtable = vtable;
    (*vtable).offset = 0;
    (*vtable).hashcode261036375 = std_Object_hashcode415158377;
    (*vtable).equals2092643753 = std_Object_equals2048201541;
    (*vtable).drop115478603 = std_Object_drop269600605;
    (*vtable).toString1664535608 = std_Object_toString1510413606;
    (*output).getClass2062954782 = std_Object_getClass2077890512;
    (*output).info = (struct class_std_ClassInfo790192477*) {0};
    (*output).references = (long int) {0};
    return output;
}



struct class_std_ClassInfo790192477;

struct class_std_ClassInfo790192477_vtable {
	int offset;
	int (*hashcode261036375) (void*);
	int (*equals2092643753) (void*, struct class_std_Object428668964*);
	void (*drop115478603) (void*);
	struct class_std_String297286482* (*toString1664535608) (void*);
	int (*equals1008747401) (void*, struct class_std_ClassInfo790192477*);
};
struct class_std_ClassInfo790192477 {
	struct class_std_ClassInfo790192477_vtable* vtable;
	struct class_std_ClassInfo790192477* info;
	long int references;
	struct class_std_ClassInfo790192477* (*getClass2062954782) (void*);
	struct class_std_String297286482* name;
	struct class_std_ClassInfo790192477* parent;
	int classHash;
	struct class_std_String297286482* (*getName37078109) (void*);
	int (*is906837228) (void*, struct class_std_Object428668964*);
};

struct class_std_String297286482* std_ClassInfo_getName1507349583(void*);
int std_ClassInfo_is563434246(void*, struct class_std_Object428668964*);
int std_Object_hashcode415158377(void*);
int std_ClassInfo_equals732052069(void*, struct class_std_Object428668964*);
void std_Object_drop269600605(void*);
struct class_std_String297286482* std_Object_toString1510413606(void*);
int std_ClassInfo_equals1815948421(void*, struct class_std_ClassInfo790192477*);

struct class_std_ClassInfo790192477* construct_std_ClassInfo812944396_2065522506(void*, struct class_std_String297286482*, struct class_std_ClassInfo790192477*, int);

static struct class_std_ClassInfo790192477* class_std_ClassInfo790192477_init93999288() {
    struct class_std_ClassInfo790192477* output;
    output = calloc(1, sizeof(struct class_std_ClassInfo790192477));
    struct class_std_ClassInfo790192477_vtable* vtable;
    vtable = malloc(sizeof(struct class_std_ClassInfo790192477_vtable));
    (*output).vtable = vtable;
    (*vtable).offset = 0;
    (*vtable).hashcode261036375 = std_Object_hashcode415158377;
    (*vtable).equals2092643753 = std_ClassInfo_equals732052069;
    (*vtable).drop115478603 = std_Object_drop269600605;
    (*vtable).toString1664535608 = std_Object_toString1510413606;
    (*vtable).equals1008747401 = std_ClassInfo_equals1815948421;
    (*output).getClass2062954782 = std_Object_getClass2077890512;
    (*output).getName37078109 = std_ClassInfo_getName1507349583;
    (*output).is906837228 = std_ClassInfo_is563434246;
    (*output).info = output;
    (*output).references = (long int) {0};
    (*output).name = (struct class_std_String297286482*) {0};
    (*output).parent = (struct class_std_ClassInfo790192477*) {0};
    (*output).classHash = (int) {0};
    return output;
}




static int super_std_Object_equals204820154197235009(void* __this, struct class_std_Object428668964* other) {
    struct class_std_ClassInfo790192477* this = __this;
    struct class_std_Object428668964* super = __this;
    int (*old) (void*, struct class_std_Object428668964*);
    old = (*(*this).vtable).equals2092643753;
    (*(*this).vtable).equals2092643753 = std_Object_equals2048201541;
    int output;
    output = (*(*this).vtable).equals2092643753(__this, other);
    (*(*this).vtable).equals2092643753 = old;
    return output;
}

struct class_std_String297286482;

struct class_std_String297286482_vtable {
	int offset;
	int (*hashcode261036375) (void*);
	int (*equals2092643753) (void*, struct class_std_Object428668964*);
	void (*drop115478603) (void*);
	struct class_std_String297286482* (*toString1664535608) (void*);
};
struct class_std_String297286482 {
	struct class_std_String297286482_vtable* vtable;
	struct class_std_ClassInfo790192477* info;
	long int references;
	struct class_std_ClassInfo790192477* (*getClass2062954782) (void*);
	char* backingPtr;
	int length;
	struct class_std_String297286482* (*concat872162341) (void*, struct class_std_String297286482*);
	struct class_std_String297286482* (*concat1663899886) (void*, char*);
	const char* (*getCStr36737184) (void*);
};

struct class_std_String297286482* std_String_concat808768211(void*, struct class_std_String297286482*);
struct class_std_String297286482* std_String_concat1600505756(void*, char*);
const char* std_String_getCStr100131314(void*);
int std_Object_hashcode415158377(void*);
int std_Object_equals2048201541(void*, struct class_std_Object428668964*);
void std_String_drop178872733(void*);
struct class_std_String297286482* std_Object_toString1510413606(void*);

struct class_std_String297286482* construct_std_String692342164_854793786(void*, const char*);
struct class_std_String297286482* construct_std_String1_346378719(void*);

static struct class_std_String297286482* class_std_String297286482_init1908093117() {
    struct class_std_String297286482* output;
    output = calloc(1, sizeof(struct class_std_String297286482));
    struct class_std_String297286482_vtable* vtable;
    vtable = malloc(sizeof(struct class_std_String297286482_vtable));
    (*output).vtable = vtable;
    (*vtable).offset = 0;
    (*vtable).hashcode261036375 = std_Object_hashcode415158377;
    (*vtable).equals2092643753 = std_Object_equals2048201541;
    (*vtable).drop115478603 = std_String_drop178872733;
    (*vtable).toString1664535608 = std_Object_toString1510413606;
    (*output).getClass2062954782 = std_Object_getClass2077890512;
    (*output).concat872162341 = std_String_concat808768211;
    (*output).concat1663899886 = std_String_concat1600505756;
    (*output).getCStr36737184 = std_String_getCStr100131314;
    (*output).info = (struct class_std_ClassInfo790192477*) {0};
    (*output).references = (long int) {0};
    (*output).backingPtr = (char*) {0};
    (*output).length = (int) {0};
    return output;
}





static void super_std_Object_drop2696006052105751929(void* __this) {
    struct class_std_String297286482* this = __this;
    struct class_std_Object428668964* super = __this;
    void (*old) (void*);
    old = (*(*this).vtable).drop115478603;
    (*(*this).vtable).drop115478603 = std_Object_drop269600605;
    (*(*this).vtable).drop115478603(__this);
    (*(*this).vtable).drop115478603 = old;
}

struct class_animals_animal701035227;

struct class_animals_animal701035227_vtable {
	int offset;
	int (*hashcode261036375) (void*);
	int (*equals2092643753) (void*, struct class_std_Object428668964*);
	void (*drop115478603) (void*);
	struct class_std_String297286482* (*toString1664535608) (void*);
	void (*says115909444) (void*);
};
struct class_animals_animal701035227 {
	struct class_animals_animal701035227_vtable* vtable;
	struct class_std_ClassInfo790192477* info;
	long int references;
	struct class_std_ClassInfo790192477* (*getClass2062954782) (void*);
	char* species;
	int numberOfLegs;
	int (*getNumberOfLegs1629143273) (void*);
};

int animals_animal_getNumberOfLegs1600807095(void*);
int std_Object_hashcode415158377(void*);
int std_Object_equals2048201541(void*, struct class_std_Object428668964*);
void std_Object_drop269600605(void*);
struct class_std_String297286482* std_Object_toString1510413606(void*);
void animals_animal_says144245622(void*);

struct class_animals_animal701035227* construct_animals_animal1441246708_616663600(void*, char*, int);

static struct class_animals_animal701035227* class_animals_animal701035227_init358873077() {
    struct class_animals_animal701035227* output;
    output = calloc(1, sizeof(struct class_animals_animal701035227));
    struct class_animals_animal701035227_vtable* vtable;
    vtable = malloc(sizeof(struct class_animals_animal701035227_vtable));
    (*output).vtable = vtable;
    (*vtable).offset = 0;
    (*vtable).hashcode261036375 = std_Object_hashcode415158377;
    (*vtable).equals2092643753 = std_Object_equals2048201541;
    (*vtable).drop115478603 = std_Object_drop269600605;
    (*vtable).toString1664535608 = std_Object_toString1510413606;
    (*vtable).says115909444 = animals_animal_says144245622;
    (*output).getClass2062954782 = std_Object_getClass2077890512;
    (*output).getNumberOfLegs1629143273 = animals_animal_getNumberOfLegs1600807095;
    (*output).info = (struct class_std_ClassInfo790192477*) {0};
    (*output).references = (long int) {0};
    (*output).species = (char*) {0};
    (*output).numberOfLegs = (int) {0};
    return output;
}

int animals_animal_getNumberOfLegs1600807095(void* __this) {
    struct class_animals_animal701035227* this = __this;
    struct class_std_Object428668964* super = __this;
    return (*this).numberOfLegs;
}




struct class_animals_animal701035227* construct_animals_animal1441246708_616663600(void* __this, char* species, int numberOfLegs) {
    struct class_animals_animal701035227* this = (struct class_animals_animal701035227*) __this;
    (*this).species = species;
    (*this).numberOfLegs = numberOfLegs;
    return this;
}

void animals_animal_says144245622(void* __this) {
    struct class_animals_animal701035227* this = __this;
    struct class_std_Object428668964* super = __this;
    print((*this).species);
    print();
}


struct class_animals_quadAnimal1032767820;

struct class_animals_quadAnimal1032767820_vtable {
	int offset;
	int (*hashcode261036375) (void*);
	int (*equals2092643753) (void*, struct class_std_Object428668964*);
	void (*drop115478603) (void*);
	struct class_std_String297286482* (*toString1664535608) (void*);
	void (*says115909444) (void*);
};
struct class_animals_quadAnimal1032767820 {
	struct class_animals_quadAnimal1032767820_vtable* vtable;
	struct class_std_ClassInfo790192477* info;
	long int references;
	struct class_std_ClassInfo790192477* (*getClass2062954782) (void*);
	char* species;
	int numberOfLegs;
	int (*getNumberOfLegs1629143273) (void*);
};

int std_Object_hashcode415158377(void*);
int std_Object_equals2048201541(void*, struct class_std_Object428668964*);
void std_Object_drop269600605(void*);
struct class_std_String297286482* std_Object_toString1510413606(void*);
void animals_quadAnimal_says208631722(void*);

struct class_animals_quadAnimal1032767820* construct_animals_quadAnimal1712536315_255037822(void*, char*);

static struct class_animals_quadAnimal1032767820* class_animals_quadAnimal1032767820_init1585014799() {
    struct class_animals_quadAnimal1032767820* output;
    output = calloc(1, sizeof(struct class_animals_quadAnimal1032767820));
    struct class_animals_quadAnimal1032767820_vtable* vtable;
    vtable = malloc(sizeof(struct class_animals_quadAnimal1032767820_vtable));
    (*output).vtable = vtable;
    (*vtable).offset = 0;
    (*vtable).hashcode261036375 = std_Object_hashcode415158377;
    (*vtable).equals2092643753 = std_Object_equals2048201541;
    (*vtable).drop115478603 = std_Object_drop269600605;
    (*vtable).toString1664535608 = std_Object_toString1510413606;
    (*vtable).says115909444 = animals_quadAnimal_says208631722;
    (*output).getClass2062954782 = std_Object_getClass2077890512;
    (*output).getNumberOfLegs1629143273 = animals_animal_getNumberOfLegs1600807095;
    (*output).info = (struct class_std_ClassInfo790192477*) {0};
    (*output).references = (long int) {0};
    (*output).species = (char*) {0};
    (*output).numberOfLegs = (int) {0};
    return output;
}



struct class_animals_quadAnimal1032767820* construct_animals_quadAnimal1712536315_255037822(void* __this, char* name) {
    construct_animals_animal1441246708_616663600(__this, name, 4);
    struct class_animals_quadAnimal1032767820* this = (struct class_animals_quadAnimal1032767820*) __this;
    return this;
}

static void super_animals_animal_says144245622914641041(void* __this) {
    struct class_animals_quadAnimal1032767820* this = __this;
    struct class_animals_animal701035227* super = __this;
    void (*old) (void*);
    old = (*(*this).vtable).says115909444;
    (*(*this).vtable).says115909444 = animals_animal_says144245622;
    (*(*this).vtable).says115909444(__this);
    (*(*this).vtable).says115909444 = old;
}

void animals_quadAnimal_says208631722(void* __this) {
    struct class_animals_quadAnimal1032767820* this = __this;
    struct class_animals_animal701035227* super = __this;
    super_animals_animal_says144245622914641041(super);
    print();
}


struct class_animals_domesticated1735632551;

struct class_animals_domesticated1735632551_vtable {
	int offset;
	int (*hashcode261036375) (void*);
	int (*equals2092643753) (void*, struct class_std_Object428668964*);
	void (*drop115478603) (void*);
	struct class_std_String297286482* (*toString1664535608) (void*);
	void (*says115909444) (void*);
};
struct class_animals_domesticated1735632551 {
	struct class_animals_domesticated1735632551_vtable* vtable;
	struct class_std_ClassInfo790192477* info;
	long int references;
	struct class_std_ClassInfo790192477* (*getClass2062954782) (void*);
	char* species;
	int numberOfLegs;
	int (*getNumberOfLegs1629143273) (void*);
	char* name;
	char* (*getName37078109) (void*);
};

char* animals_domesticated_getName1318038287(void*);
int std_Object_hashcode415158377(void*);
int std_Object_equals2048201541(void*, struct class_std_Object428668964*);
void std_Object_drop269600605(void*);
struct class_std_String297286482* std_Object_toString1510413606(void*);
void animals_domesticated_says1396869622(void*);

struct class_animals_domesticated1735632551* construct_animals_domesticated1183868447_1783070894(void*, char*, char*);

static struct class_animals_domesticated1735632551* class_animals_domesticated1735632551_init1111655878() {
    struct class_animals_domesticated1735632551* output;
    output = calloc(1, sizeof(struct class_animals_domesticated1735632551));
    struct class_animals_domesticated1735632551_vtable* vtable;
    vtable = malloc(sizeof(struct class_animals_domesticated1735632551_vtable));
    (*output).vtable = vtable;
    (*vtable).offset = 0;
    (*vtable).hashcode261036375 = std_Object_hashcode415158377;
    (*vtable).equals2092643753 = std_Object_equals2048201541;
    (*vtable).drop115478603 = std_Object_drop269600605;
    (*vtable).toString1664535608 = std_Object_toString1510413606;
    (*vtable).says115909444 = animals_domesticated_says1396869622;
    (*output).getClass2062954782 = std_Object_getClass2077890512;
    (*output).getNumberOfLegs1629143273 = animals_animal_getNumberOfLegs1600807095;
    (*output).getName37078109 = animals_domesticated_getName1318038287;
    (*output).info = (struct class_std_ClassInfo790192477*) {0};
    (*output).references = (long int) {0};
    (*output).species = (char*) {0};
    (*output).numberOfLegs = (int) {0};
    (*output).name = (char*) {0};
    return output;
}

char* animals_domesticated_getName1318038287(void* __this) {
    struct class_animals_domesticated1735632551* this = __this;
    struct class_animals_quadAnimal1032767820* super = __this;
    return (*this).name;
}




struct class_animals_domesticated1735632551* construct_animals_domesticated1183868447_1783070894(void* __this, char* name, char* species) {
    construct_animals_quadAnimal1712536315_255037822(__this, species);
    struct class_animals_domesticated1735632551* this = (struct class_animals_domesticated1735632551*) __this;
    (*this).name = name;
    return this;
}

static void super_animals_quadAnimal_says2086317221661424852(void* __this) {
    struct class_animals_domesticated1735632551* this = __this;
    struct class_animals_quadAnimal1032767820* super = __this;
    void (*old) (void*);
    old = (*(*this).vtable).says115909444;
    (*(*this).vtable).says115909444 = animals_quadAnimal_says208631722;
    (*(*this).vtable).says115909444(__this);
    (*(*this).vtable).says115909444 = old;
}

void animals_domesticated_says1396869622(void* __this) {
    struct class_animals_domesticated1735632551* this = __this;
    struct class_animals_quadAnimal1032767820* super = __this;
    print((*this).getName37078109(this));
    print();
    super_animals_quadAnimal_says2086317221661424852(super);
    print();
}


struct class_animals_dog712180837;

struct class_animals_dog712180837_vtable {
	int offset;
	int (*hashcode261036375) (void*);
	int (*equals2092643753) (void*, struct class_std_Object428668964*);
	void (*drop115478603) (void*);
	struct class_std_String297286482* (*toString1664535608) (void*);
	void (*says115909444) (void*);
};
struct class_animals_dog712180837 {
	struct class_animals_dog712180837_vtable* vtable;
	struct class_std_ClassInfo790192477* info;
	long int references;
	struct class_std_ClassInfo790192477* (*getClass2062954782) (void*);
	char* species;
	int numberOfLegs;
	int (*getNumberOfLegs1629143273) (void*);
	char* name;
	char* (*getName37078109) (void*);
};

int std_Object_hashcode415158377(void*);
int std_Object_equals2048201541(void*, struct class_std_Object428668964*);
void std_Object_drop269600605(void*);
struct class_std_String297286482* std_Object_toString1510413606(void*);
void animals_domesticated_says1396869622(void*);

struct class_animals_dog712180837* construct_animals_dog606548772_270638475(void*, char*);
struct class_animals_dog712180837* construct_animals_dog1_1941176109(void*);

static struct class_animals_dog712180837* class_animals_dog712180837_init1445296639() {
    struct class_animals_dog712180837* output;
    output = calloc(1, sizeof(struct class_animals_dog712180837));
    struct class_animals_dog712180837_vtable* vtable;
    vtable = malloc(sizeof(struct class_animals_dog712180837_vtable));
    (*output).vtable = vtable;
    (*vtable).offset = 0;
    (*vtable).hashcode261036375 = std_Object_hashcode415158377;
    (*vtable).equals2092643753 = std_Object_equals2048201541;
    (*vtable).drop115478603 = std_Object_drop269600605;
    (*vtable).toString1664535608 = std_Object_toString1510413606;
    (*vtable).says115909444 = animals_domesticated_says1396869622;
    (*output).getClass2062954782 = std_Object_getClass2077890512;
    (*output).getNumberOfLegs1629143273 = animals_animal_getNumberOfLegs1600807095;
    (*output).getName37078109 = animals_domesticated_getName1318038287;
    (*output).info = (struct class_std_ClassInfo790192477*) {0};
    (*output).references = (long int) {0};
    (*output).species = (char*) {0};
    (*output).numberOfLegs = (int) {0};
    (*output).name = (char*) {0};
    return output;
}



struct class_animals_dog712180837* construct_animals_dog606548772_270638475(void* __this, char* name) {
    construct_animals_domesticated1183868447_1783070894(__this, name, );
    struct class_animals_dog712180837* this = (struct class_animals_dog712180837*) __this;
    return this;
}


struct class_animals_dog712180837* construct_animals_dog1_1941176109(void* __this) {
    construct_animals_dog606548772_270638475(__this, );
    struct class_animals_dog712180837* this = (struct class_animals_dog712180837*) __this;
    return this;
}

struct class_cat986725224;

struct class_cat986725224_vtable {
	int offset;
	int (*hashcode261036375) (void*);
	int (*equals2092643753) (void*, struct class_std_Object428668964*);
	void (*drop115478603) (void*);
	struct class_std_String297286482* (*toString1664535608) (void*);
	void (*says115909444) (void*);
};
struct class_cat986725224 {
	struct class_cat986725224_vtable* vtable;
	struct class_std_ClassInfo790192477* info;
	long int references;
	struct class_std_ClassInfo790192477* (*getClass2062954782) (void*);
	char* species;
	int numberOfLegs;
	int (*getNumberOfLegs1629143273) (void*);
	char* name;
	char* (*getName37078109) (void*);
};

int std_Object_hashcode415158377(void*);
int std_Object_equals2048201541(void*, struct class_std_Object428668964*);
void std_Object_drop269600605(void*);
struct class_std_String297286482* std_Object_toString1510413606(void*);
void cat_says1681994794(void*);

struct class_cat986725224* construct_cat1528637606_348971869(void*, char*);

static struct class_cat986725224* class_cat986725224_init1223297765() {
    struct class_cat986725224* output;
    output = calloc(1, sizeof(struct class_cat986725224));
    struct class_cat986725224_vtable* vtable;
    vtable = malloc(sizeof(struct class_cat986725224_vtable));
    (*output).vtable = vtable;
    (*vtable).offset = 0;
    (*vtable).hashcode261036375 = std_Object_hashcode415158377;
    (*vtable).equals2092643753 = std_Object_equals2048201541;
    (*vtable).drop115478603 = std_Object_drop269600605;
    (*vtable).toString1664535608 = std_Object_toString1510413606;
    (*vtable).says115909444 = cat_says1681994794;
    (*output).getClass2062954782 = std_Object_getClass2077890512;
    (*output).getNumberOfLegs1629143273 = animals_animal_getNumberOfLegs1600807095;
    (*output).getName37078109 = animals_domesticated_getName1318038287;
    (*output).info = (struct class_std_ClassInfo790192477*) {0};
    (*output).references = (long int) {0};
    (*output).species = (char*) {0};
    (*output).numberOfLegs = (int) {0};
    (*output).name = (char*) {0};
    return output;
}



struct class_cat986725224* construct_cat1528637606_348971869(void* __this, char* name) {
    construct_animals_domesticated1183868447_1783070894(__this, name, );
    struct class_cat986725224* this = (struct class_cat986725224*) __this;
    return this;
}

static void super_animals_domesticated_says1396869622869791931(void* __this) {
    struct class_cat986725224* this = __this;
    struct class_animals_domesticated1735632551* super = __this;
    void (*old) (void*);
    old = (*(*this).vtable).says115909444;
    (*(*this).vtable).says115909444 = animals_domesticated_says1396869622;
    (*(*this).vtable).says115909444(__this);
    (*(*this).vtable).says115909444 = old;
}

void cat_says1681994794(void* __this) {
    struct class_cat986725224* this = __this;
    struct class_animals_domesticated1735632551* super = __this;
    println();
}


