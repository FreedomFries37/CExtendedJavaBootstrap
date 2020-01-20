struct class_std_ClassInfo324399139;
struct class_std_String817305134;
void* malloc(unsigned int);
void* calloc(unsigned int, unsigned int);
void free(void*);
void print(const char*);
void println(const char*);
void print_s(struct class_std_String817305134*);
void println_s(struct class_std_String817305134*);
struct class_std_Object685922652;

struct class_std_Object685922652_vtable {
	int offset;
	int (*hashcode261036375) (void*);
	int (*equals1053723765) (void*, struct class_std_Object685922652*);
	void (*drop115478603) (void*);
	struct class_std_String817305134* (*toString1664535608) (void*);
};
struct class_std_Object685922652 {
	struct class_std_Object685922652_vtable* vtable;
	struct class_std_ClassInfo324399139* info;
	long int references;
	struct class_std_ClassInfo324399139* (*getClass2062954782) (void*);
};

struct class_std_ClassInfo324399139* std_Object_getClass64777540(void*);
int std_Object_hashcode1866695947(void*);
int std_Object_equals1074008557(void*, struct class_std_Object685922652*);
void std_Object_drop2012253719(void*);
struct class_std_String817305134* std_Object_toString502699366(void*);

struct class_std_Object685922652* construct_std_Object1_330993863(void*);

static struct class_std_Object685922652* class_std_Object685922652_init577347010() {
    struct class_std_Object685922652* output;
    output = calloc(1, sizeof(struct class_std_Object685922652));
    struct class_std_Object685922652_vtable* vtable;
    vtable = malloc(sizeof(struct class_std_Object685922652_vtable));
    (*output).vtable = vtable;
    (*vtable).offset = 0;
    (*vtable).hashcode261036375 = std_Object_hashcode1866695947;
    (*vtable).equals1053723765 = std_Object_equals1074008557;
    (*vtable).drop115478603 = std_Object_drop2012253719;
    (*vtable).toString1664535608 = std_Object_toString502699366;
    (*output).getClass2062954782 = std_Object_getClass64777540;
    (*output).info = (struct class_std_ClassInfo324399139*) {0};
    (*output).references = (long int) {0};
    return output;
}



struct class_std_ClassInfo324399139;

struct class_std_ClassInfo324399139_vtable {
	int offset;
	int (*hashcode261036375) (void*);
	int (*equals1053723765) (void*, struct class_std_Object685922652*);
	void (*drop115478603) (void*);
	struct class_std_String817305134* (*toString1664535608) (void*);
	int (*equals1724766376) (void*, struct class_std_ClassInfo324399139*);
};
struct class_std_ClassInfo324399139 {
	struct class_std_ClassInfo324399139_vtable* vtable;
	struct class_std_ClassInfo324399139* info;
	long int references;
	struct class_std_ClassInfo324399139* (*getClass2062954782) (void*);
	struct class_std_String817305134* name;
	struct class_std_ClassInfo324399139* parent;
	int classHash;
	struct class_std_String817305134* (*getName37078109) (void*);
	int (*is1945757216) (void*, struct class_std_Object685922652*);
};

struct class_std_String817305134* std_ClassInfo_getName774504741(void*);
int std_ClassInfo_is1537627230(void*, struct class_std_Object685922652*);
int std_Object_hashcode1866695947(void*);
int std_ClassInfo_equals242140915(void*, struct class_std_Object685922652*);
void std_Object_drop2012253719(void*);
struct class_std_String817305134* std_Object_toString502699366(void*);
int std_ClassInfo_equals913183526(void*, struct class_std_ClassInfo324399139*);

struct class_std_ClassInfo324399139* construct_std_ClassInfo1798510245_746826879(void*, struct class_std_String817305134*, struct class_std_ClassInfo324399139*, int);

static struct class_std_ClassInfo324399139* class_std_ClassInfo324399139_init1711871859() {
    struct class_std_ClassInfo324399139* output;
    output = calloc(1, sizeof(struct class_std_ClassInfo324399139));
    struct class_std_ClassInfo324399139_vtable* vtable;
    vtable = malloc(sizeof(struct class_std_ClassInfo324399139_vtable));
    (*output).vtable = vtable;
    (*vtable).offset = 0;
    (*vtable).hashcode261036375 = std_Object_hashcode1866695947;
    (*vtable).equals1053723765 = std_ClassInfo_equals242140915;
    (*vtable).drop115478603 = std_Object_drop2012253719;
    (*vtable).toString1664535608 = std_Object_toString502699366;
    (*vtable).equals1724766376 = std_ClassInfo_equals913183526;
    (*output).getClass2062954782 = std_Object_getClass64777540;
    (*output).getName37078109 = std_ClassInfo_getName774504741;
    (*output).is1945757216 = std_ClassInfo_is1537627230;
    (*output).info = output;
    (*output).references = (long int) {0};
    (*output).name = (struct class_std_String817305134*) {0};
    (*output).parent = (struct class_std_ClassInfo324399139*) {0};
    (*output).classHash = (int) {0};
    return output;
}




static int super_std_Object_equals10740085571792730991(void* __this, struct class_std_Object685922652* other) {
    struct class_std_ClassInfo324399139* this = __this;
    struct class_std_Object685922652* super = __this;
    int (*old) (void*, struct class_std_Object685922652*);
    old = (*(*this).vtable).equals1053723765;
    (*(*this).vtable).equals1053723765 = std_Object_equals1074008557;
    int output;
    output = (*(*this).vtable).equals1053723765(__this, other);
    (*(*this).vtable).equals1053723765 = old;
    return output;
}

struct class_std_String817305134;

struct class_std_String817305134_vtable {
	int offset;
	int (*hashcode261036375) (void*);
	int (*equals1053723765) (void*, struct class_std_Object685922652*);
	void (*drop115478603) (void*);
	struct class_std_String817305134* (*toString1664535608) (void*);
};
struct class_std_String817305134 {
	struct class_std_String817305134_vtable* vtable;
	struct class_std_ClassInfo324399139* info;
	long int references;
	struct class_std_ClassInfo324399139* (*getClass2062954782) (void*);
	char* backingPtr;
	int length;
	struct class_std_String817305134* (*concat180943281) (void*, struct class_std_String817305134*);
	struct class_std_String817305134* (*concat1663899886) (void*, char*);
	const char* (*getCStr36737184) (void*);
};

struct class_std_String817305134* std_String_concat2037516913(void*, struct class_std_String817305134*);
struct class_std_String817305134* std_String_concat412607216(void*, char*);
const char* std_String_getCStr2113244286(void*);
int std_Object_hashcode1866695947(void*);
int std_Object_equals1074008557(void*, struct class_std_Object685922652*);
void std_String_drop2102981591(void*);
struct class_std_String817305134* std_Object_toString502699366(void*);

struct class_std_String817305134* construct_std_String1076770779_1333810398(void*, const char*);
struct class_std_String817305134* construct_std_String1_2128077333(void*);

static struct class_std_String817305134* class_std_String817305134_init1324446555() {
    struct class_std_String817305134* output;
    output = calloc(1, sizeof(struct class_std_String817305134));
    struct class_std_String817305134_vtable* vtable;
    vtable = malloc(sizeof(struct class_std_String817305134_vtable));
    (*output).vtable = vtable;
    (*vtable).offset = 0;
    (*vtable).hashcode261036375 = std_Object_hashcode1866695947;
    (*vtable).equals1053723765 = std_Object_equals1074008557;
    (*vtable).drop115478603 = std_String_drop2102981591;
    (*vtable).toString1664535608 = std_Object_toString502699366;
    (*output).getClass2062954782 = std_Object_getClass64777540;
    (*output).concat180943281 = std_String_concat2037516913;
    (*output).concat1663899886 = std_String_concat412607216;
    (*output).getCStr36737184 = std_String_getCStr2113244286;
    (*output).info = (struct class_std_ClassInfo324399139*) {0};
    (*output).references = (long int) {0};
    (*output).backingPtr = (char*) {0};
    (*output).length = (int) {0};
    return output;
}





static void super_std_Object_drop2012253719393698245(void* __this) {
    struct class_std_String817305134* this = __this;
    struct class_std_Object685922652* super = __this;
    void (*old) (void*);
    old = (*(*this).vtable).drop115478603;
    (*(*this).vtable).drop115478603 = std_Object_drop2012253719;
    (*(*this).vtable).drop115478603(__this);
    (*(*this).vtable).drop115478603 = old;
}

struct class_animals_animal413556389;

struct class_animals_animal413556389_vtable {
	int offset;
	int (*hashcode261036375) (void*);
	int (*equals1053723765) (void*, struct class_std_Object685922652*);
	void (*drop115478603) (void*);
	struct class_std_String817305134* (*toString1664535608) (void*);
	void (*says115909444) (void*);
};
struct class_animals_animal413556389 {
	struct class_animals_animal413556389_vtable* vtable;
	struct class_std_ClassInfo324399139* info;
	long int references;
	struct class_std_ClassInfo324399139* (*getClass2062954782) (void*);
	char* species;
	int numberOfLegs;
	int (*getNumberOfLegs1629143273) (void*);
};

int animals_animal_getNumberOfLegs412305877(void*);
int std_Object_hashcode1866695947(void*);
int std_Object_equals1074008557(void*, struct class_std_Object685922652*);
void std_Object_drop2012253719(void*);
struct class_std_String817305134* std_Object_toString502699366(void*);
void animals_animal_says2137608702(void*);

struct class_animals_animal413556389* construct_animals_animal1016102095_2059466806(void*, char*, int);

static struct class_animals_animal413556389* class_animals_animal413556389_init2063304608() {
    struct class_animals_animal413556389* output;
    output = calloc(1, sizeof(struct class_animals_animal413556389));
    struct class_animals_animal413556389_vtable* vtable;
    vtable = malloc(sizeof(struct class_animals_animal413556389_vtable));
    (*output).vtable = vtable;
    (*vtable).offset = 0;
    (*vtable).hashcode261036375 = std_Object_hashcode1866695947;
    (*vtable).equals1053723765 = std_Object_equals1074008557;
    (*vtable).drop115478603 = std_Object_drop2012253719;
    (*vtable).toString1664535608 = std_Object_toString502699366;
    (*vtable).says115909444 = animals_animal_says2137608702;
    (*output).getClass2062954782 = std_Object_getClass64777540;
    (*output).getNumberOfLegs1629143273 = animals_animal_getNumberOfLegs412305877;
    (*output).info = (struct class_std_ClassInfo324399139*) {0};
    (*output).references = (long int) {0};
    (*output).species = (char*) {0};
    (*output).numberOfLegs = (int) {0};
    return output;
}

int animals_animal_getNumberOfLegs412305877(void* __this) {
    struct class_animals_animal413556389* this = __this;
    struct class_std_Object685922652* super = __this;
    return (*this).numberOfLegs;
}




struct class_animals_animal413556389* construct_animals_animal1016102095_2059466806(void* __this, char* species, int numberOfLegs) {
    struct class_animals_animal413556389* this = (struct class_animals_animal413556389*) __this;
    (*this).species = species;
    (*this).numberOfLegs = numberOfLegs;
    return this;
}

void animals_animal_says2137608702(void* __this) {
    struct class_animals_animal413556389* this = __this;
    struct class_std_Object685922652* super = __this;
    print((*this).species);
    print();
}


struct class_animals_quadAnimal2147359436;

struct class_animals_quadAnimal2147359436_vtable {
	int offset;
	int (*hashcode261036375) (void*);
	int (*equals1053723765) (void*, struct class_std_Object685922652*);
	void (*drop115478603) (void*);
	struct class_std_String817305134* (*toString1664535608) (void*);
	void (*says115909444) (void*);
};
struct class_animals_quadAnimal2147359436 {
	struct class_animals_quadAnimal2147359436_vtable* vtable;
	struct class_std_ClassInfo324399139* info;
	long int references;
	struct class_std_ClassInfo324399139* (*getClass2062954782) (void*);
	char* species;
	int numberOfLegs;
	int (*getNumberOfLegs1629143273) (void*);
};

int std_Object_hashcode1866695947(void*);
int std_Object_equals1074008557(void*, struct class_std_Object685922652*);
void std_Object_drop2012253719(void*);
struct class_std_String817305134* std_Object_toString502699366(void*);
void animals_quadAnimal_says1804481250(void*);

struct class_animals_quadAnimal2147359436* construct_animals_quadAnimal1519736196_1246740908(void*, char*);

static struct class_animals_quadAnimal2147359436* class_animals_quadAnimal2147359436_init1502385461() {
    struct class_animals_quadAnimal2147359436* output;
    output = calloc(1, sizeof(struct class_animals_quadAnimal2147359436));
    struct class_animals_quadAnimal2147359436_vtable* vtable;
    vtable = malloc(sizeof(struct class_animals_quadAnimal2147359436_vtable));
    (*output).vtable = vtable;
    (*vtable).offset = 0;
    (*vtable).hashcode261036375 = std_Object_hashcode1866695947;
    (*vtable).equals1053723765 = std_Object_equals1074008557;
    (*vtable).drop115478603 = std_Object_drop2012253719;
    (*vtable).toString1664535608 = std_Object_toString502699366;
    (*vtable).says115909444 = animals_quadAnimal_says1804481250;
    (*output).getClass2062954782 = std_Object_getClass64777540;
    (*output).getNumberOfLegs1629143273 = animals_animal_getNumberOfLegs412305877;
    (*output).info = (struct class_std_ClassInfo324399139*) {0};
    (*output).references = (long int) {0};
    (*output).species = (char*) {0};
    (*output).numberOfLegs = (int) {0};
    return output;
}



struct class_animals_quadAnimal2147359436* construct_animals_quadAnimal1519736196_1246740908(void* __this, char* name) {
    construct_animals_animal1016102095_2059466806(__this, name, 4);
    struct class_animals_quadAnimal2147359436* this = (struct class_animals_quadAnimal2147359436*) __this;
    return this;
}

static void super_animals_animal_says2137608702909154717(void* __this) {
    struct class_animals_quadAnimal2147359436* this = __this;
    struct class_animals_animal413556389* super = __this;
    void (*old) (void*);
    old = (*(*this).vtable).says115909444;
    (*(*this).vtable).says115909444 = animals_animal_says2137608702;
    (*(*this).vtable).says115909444(__this);
    (*(*this).vtable).says115909444 = old;
}

void animals_quadAnimal_says1804481250(void* __this) {
    struct class_animals_quadAnimal2147359436* this = __this;
    struct class_animals_animal413556389* super = __this;
    super_animals_animal_says2137608702909154717(super);
    print();
}


struct class_animals_domesticated621040935;

struct class_animals_domesticated621040935_vtable {
	int offset;
	int (*hashcode261036375) (void*);
	int (*equals1053723765) (void*, struct class_std_Object685922652*);
	void (*drop115478603) (void*);
	struct class_std_String817305134* (*toString1664535608) (void*);
	void (*says115909444) (void*);
};
struct class_animals_domesticated621040935 {
	struct class_animals_domesticated621040935_vtable* vtable;
	struct class_std_ClassInfo324399139* info;
	long int references;
	struct class_std_ClassInfo324399139* (*getClass2062954782) (void*);
	char* species;
	int numberOfLegs;
	int (*getNumberOfLegs1629143273) (void*);
	char* name;
	char* (*getName37078109) (void*);
};

char* animals_domesticated_getName963816037(void*);
int std_Object_hashcode1866695947(void*);
int std_Object_equals1074008557(void*, struct class_std_Object685922652*);
void std_Object_drop2012253719(void*);
struct class_std_String817305134* std_Object_toString502699366(void*);
void animals_domesticated_says884984702(void*);

struct class_animals_domesticated621040935* construct_animals_domesticated1769396895_741983931(void*, char*, char*);

static struct class_animals_domesticated621040935* class_animals_domesticated621040935_init1611550172() {
    struct class_animals_domesticated621040935* output;
    output = calloc(1, sizeof(struct class_animals_domesticated621040935));
    struct class_animals_domesticated621040935_vtable* vtable;
    vtable = malloc(sizeof(struct class_animals_domesticated621040935_vtable));
    (*output).vtable = vtable;
    (*vtable).offset = 0;
    (*vtable).hashcode261036375 = std_Object_hashcode1866695947;
    (*vtable).equals1053723765 = std_Object_equals1074008557;
    (*vtable).drop115478603 = std_Object_drop2012253719;
    (*vtable).toString1664535608 = std_Object_toString502699366;
    (*vtable).says115909444 = animals_domesticated_says884984702;
    (*output).getClass2062954782 = std_Object_getClass64777540;
    (*output).getNumberOfLegs1629143273 = animals_animal_getNumberOfLegs412305877;
    (*output).getName37078109 = animals_domesticated_getName963816037;
    (*output).info = (struct class_std_ClassInfo324399139*) {0};
    (*output).references = (long int) {0};
    (*output).species = (char*) {0};
    (*output).numberOfLegs = (int) {0};
    (*output).name = (char*) {0};
    return output;
}

char* animals_domesticated_getName963816037(void* __this) {
    struct class_animals_domesticated621040935* this = __this;
    struct class_animals_quadAnimal2147359436* super = __this;
    return (*this).name;
}




struct class_animals_domesticated621040935* construct_animals_domesticated1769396895_741983931(void* __this, char* name, char* species) {
    construct_animals_quadAnimal1519736196_1246740908(__this, species);
    struct class_animals_domesticated621040935* this = (struct class_animals_domesticated621040935*) __this;
    (*this).name = name;
    return this;
}

static void super_animals_quadAnimal_says1804481250401238282(void* __this) {
    struct class_animals_domesticated621040935* this = __this;
    struct class_animals_quadAnimal2147359436* super = __this;
    void (*old) (void*);
    old = (*(*this).vtable).says115909444;
    (*(*this).vtable).says115909444 = animals_quadAnimal_says1804481250;
    (*(*this).vtable).says115909444(__this);
    (*(*this).vtable).says115909444 = old;
}

void animals_domesticated_says884984702(void* __this) {
    struct class_animals_domesticated621040935* this = __this;
    struct class_animals_quadAnimal2147359436* super = __this;
    print((*this).getName37078109(this));
    print();
    super_animals_quadAnimal_says1804481250401238282(super);
    print();
}


struct class_animals_dog1826772453;

struct class_animals_dog1826772453_vtable {
	int offset;
	int (*hashcode261036375) (void*);
	int (*equals1053723765) (void*, struct class_std_Object685922652*);
	void (*drop115478603) (void*);
	struct class_std_String817305134* (*toString1664535608) (void*);
	void (*says115909444) (void*);
};
struct class_animals_dog1826772453 {
	struct class_animals_dog1826772453_vtable* vtable;
	struct class_std_ClassInfo324399139* info;
	long int references;
	struct class_std_ClassInfo324399139* (*getClass2062954782) (void*);
	char* species;
	int numberOfLegs;
	int (*getNumberOfLegs1629143273) (void*);
	char* name;
	char* (*getName37078109) (void*);
};

int std_Object_hashcode1866695947(void*);
int std_Object_equals1074008557(void*, struct class_std_Object685922652*);
void std_Object_drop2012253719(void*);
struct class_std_String817305134* std_Object_toString502699366(void*);
void animals_domesticated_says884984702(void*);

struct class_animals_dog1826772453* construct_animals_dog2143437148_1266269529(void*, char*);
struct class_animals_dog1826772453* construct_animals_dog1_533279943(void*);

static struct class_animals_dog1826772453* class_animals_dog1826772453_init1975796817() {
    struct class_animals_dog1826772453* output;
    output = calloc(1, sizeof(struct class_animals_dog1826772453));
    struct class_animals_dog1826772453_vtable* vtable;
    vtable = malloc(sizeof(struct class_animals_dog1826772453_vtable));
    (*output).vtable = vtable;
    (*vtable).offset = 0;
    (*vtable).hashcode261036375 = std_Object_hashcode1866695947;
    (*vtable).equals1053723765 = std_Object_equals1074008557;
    (*vtable).drop115478603 = std_Object_drop2012253719;
    (*vtable).toString1664535608 = std_Object_toString502699366;
    (*vtable).says115909444 = animals_domesticated_says884984702;
    (*output).getClass2062954782 = std_Object_getClass64777540;
    (*output).getNumberOfLegs1629143273 = animals_animal_getNumberOfLegs412305877;
    (*output).getName37078109 = animals_domesticated_getName963816037;
    (*output).info = (struct class_std_ClassInfo324399139*) {0};
    (*output).references = (long int) {0};
    (*output).species = (char*) {0};
    (*output).numberOfLegs = (int) {0};
    (*output).name = (char*) {0};
    return output;
}



struct class_animals_dog1826772453* construct_animals_dog2143437148_1266269529(void* __this, char* name) {
    construct_animals_domesticated1769396895_741983931(__this, name, );
    struct class_animals_dog1826772453* this = (struct class_animals_dog1826772453*) __this;
    return this;
}


struct class_animals_dog1826772453* construct_animals_dog1_533279943(void* __this) {
    construct_animals_dog2143437148_1266269529(__this, );
    struct class_animals_dog1826772453* this = (struct class_animals_dog1826772453*) __this;
    return this;
}

struct class_cat1692644852;

struct class_cat1692644852_vtable {
	int offset;
	int (*hashcode261036375) (void*);
	int (*equals1053723765) (void*, struct class_std_Object685922652*);
	void (*drop115478603) (void*);
	struct class_std_String817305134* (*toString1664535608) (void*);
	void (*says115909444) (void*);
};
struct class_cat1692644852 {
	struct class_cat1692644852_vtable* vtable;
	struct class_std_ClassInfo324399139* info;
	long int references;
	struct class_std_ClassInfo324399139* (*getClass2062954782) (void*);
	char* species;
	int numberOfLegs;
	int (*getNumberOfLegs1629143273) (void*);
	char* name;
	char* (*getName37078109) (void*);
};

int std_Object_hashcode1866695947(void*);
int std_Object_equals1074008557(void*, struct class_std_Object685922652*);
void std_Object_drop2012253719(void*);
struct class_std_String817305134* std_Object_toString502699366(void*);
void cat_says138516450(void*);

struct class_cat1692644852* construct_cat260840956_1823163445(void*, char*);

static struct class_cat1692644852* class_cat1692644852_init1716779587() {
    struct class_cat1692644852* output;
    output = calloc(1, sizeof(struct class_cat1692644852));
    struct class_cat1692644852_vtable* vtable;
    vtable = malloc(sizeof(struct class_cat1692644852_vtable));
    (*output).vtable = vtable;
    (*vtable).offset = 0;
    (*vtable).hashcode261036375 = std_Object_hashcode1866695947;
    (*vtable).equals1053723765 = std_Object_equals1074008557;
    (*vtable).drop115478603 = std_Object_drop2012253719;
    (*vtable).toString1664535608 = std_Object_toString502699366;
    (*vtable).says115909444 = cat_says138516450;
    (*output).getClass2062954782 = std_Object_getClass64777540;
    (*output).getNumberOfLegs1629143273 = animals_animal_getNumberOfLegs412305877;
    (*output).getName37078109 = animals_domesticated_getName963816037;
    (*output).info = (struct class_std_ClassInfo324399139*) {0};
    (*output).references = (long int) {0};
    (*output).species = (char*) {0};
    (*output).numberOfLegs = (int) {0};
    (*output).name = (char*) {0};
    return output;
}



struct class_cat1692644852* construct_cat260840956_1823163445(void* __this, char* name) {
    construct_animals_domesticated1769396895_741983931(__this, name, );
    struct class_cat1692644852* this = (struct class_cat1692644852*) __this;
    return this;
}

static void super_animals_domesticated_says884984702397953829(void* __this) {
    struct class_cat1692644852* this = __this;
    struct class_animals_domesticated621040935* super = __this;
    void (*old) (void*);
    old = (*(*this).vtable).says115909444;
    (*(*this).vtable).says115909444 = animals_domesticated_says884984702;
    (*(*this).vtable).says115909444(__this);
    (*(*this).vtable).says115909444 = old;
}

void cat_says138516450(void* __this) {
    struct class_cat1692644852* this = __this;
    struct class_animals_domesticated621040935* super = __this;
    println();
}


