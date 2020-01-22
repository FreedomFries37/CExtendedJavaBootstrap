struct class_std_ClassInfo804771843;
struct class_std_String1297677838;
typedef unsigned long int class_id;
void* malloc(unsigned int);
void* calloc(unsigned int, unsigned int);
void free(void*);
void print(const char*);
void println(const char*);
void print_s(struct class_std_String1297677838*);
void println_s(struct class_std_String1297677838*);
struct class_std_ClassInfo804771843* getClass(class_id);
struct class_std_Object1166295356;

struct class_std_Object1166295356_vtable {
	int offset;
	int (*hashcode261036375) (void*);
	int (*equals2039369046) (void*, struct class_std_Object1166295356*);
	void (*drop115478603) (void*);
	struct class_std_String1297677838* (*toString1664535608) (void*);
};
struct class_std_Object1166295356 {
	struct class_std_Object1166295356_vtable* vtable;
	struct class_std_ClassInfo804771843* info;
	long int references;
	struct class_std_ClassInfo804771843* (*getClass2062954782) (void*);
};

struct class_std_ClassInfo804771843* std_Object_getClass247095813(void*);
int std_Object_hashcode2049014220(void*);
int std_Object_equals270681549(void*, struct class_std_Object1166295356*);
void std_Object_drop2100395304(void*);
struct class_std_String1297677838* std_Object_toString320381093(void*);

struct class_std_Object1166295356* construct_std_Object1_1857976346(void*);

static struct class_std_Object1166295356* class_std_Object1166295356_init635364512() {
    struct class_std_Object1166295356* output;
    output = calloc(1, sizeof(struct class_std_Object1166295356));
    struct class_std_Object1166295356_vtable* vtable;
    vtable = malloc(sizeof(struct class_std_Object1166295356_vtable));
    (*output).vtable = vtable;
    (*vtable).offset = 0;
    (*vtable).hashcode261036375 = std_Object_hashcode2049014220;
    (*vtable).equals2039369046 = std_Object_equals270681549;
    (*vtable).drop115478603 = std_Object_drop2100395304;
    (*vtable).toString1664535608 = std_Object_toString320381093;
    (*output).getClass2062954782 = std_Object_getClass247095813;
    (*output).info = (struct class_std_ClassInfo804771843*) {0};
    (*output).references = (long int) {0};
    return output;
}



struct class_std_ClassInfo804771843;

struct class_std_ClassInfo804771843_vtable {
	int offset;
	int (*hashcode261036375) (void*);
	int (*equals2039369046) (void*, struct class_std_Object1166295356*);
	void (*drop115478603) (void*);
	struct class_std_String1297677838* (*toString1664535608) (void*);
	int (*equals747249249) (void*, struct class_std_ClassInfo804771843*);
};
struct class_std_ClassInfo804771843 {
	struct class_std_ClassInfo804771843_vtable* vtable;
	struct class_std_ClassInfo804771843* info;
	long int references;
	struct class_std_ClassInfo804771843* (*getClass2062954782) (void*);
	struct class_std_String1297677838* name;
	struct class_std_ClassInfo804771843* parent;
	int classHash;
	struct class_std_String1297677838* (*getName37078109) (void*);
	int (*is960111935) (void*, struct class_std_Object1166295356*);
};

struct class_std_String1297677838* std_ClassInfo_getName956823014(void*);
int std_ClassInfo_is1954013058(void*, struct class_std_Object1166295356*);
int std_Object_hashcode2049014220(void*);
int std_ClassInfo_equals1045467923(void*, struct class_std_Object1166295356*);
void std_Object_drop2100395304(void*);
struct class_std_String1297677838* std_Object_toString320381093(void*);
int std_ClassInfo_equals1741150372(void*, struct class_std_ClassInfo804771843*);

struct class_std_ClassInfo804771843* construct_std_ClassInfo1202830901_1447135283(void*, struct class_std_String1297677838*, struct class_std_ClassInfo804771843*, int);

static struct class_std_ClassInfo804771843* class_std_ClassInfo804771843_init501931049() {
    struct class_std_ClassInfo804771843* output;
    output = calloc(1, sizeof(struct class_std_ClassInfo804771843));
    struct class_std_ClassInfo804771843_vtable* vtable;
    vtable = malloc(sizeof(struct class_std_ClassInfo804771843_vtable));
    (*output).vtable = vtable;
    (*vtable).offset = 0;
    (*vtable).hashcode261036375 = std_Object_hashcode2049014220;
    (*vtable).equals2039369046 = std_ClassInfo_equals1045467923;
    (*vtable).drop115478603 = std_Object_drop2100395304;
    (*vtable).toString1664535608 = std_Object_toString320381093;
    (*vtable).equals747249249 = std_ClassInfo_equals1741150372;
    (*output).getClass2062954782 = std_Object_getClass247095813;
    (*output).getName37078109 = std_ClassInfo_getName956823014;
    (*output).is960111935 = std_ClassInfo_is1954013058;
    (*output).info = output;
    (*output).references = (long int) {0};
    (*output).name = (struct class_std_String1297677838*) {0};
    (*output).parent = (struct class_std_ClassInfo804771843*) {0};
    (*output).classHash = (int) {0};
    return output;
}




static int super_std_Object_equals270681549558247374(void* __this, struct class_std_Object1166295356* other) {
    struct class_std_ClassInfo804771843* this = __this;
    struct class_std_Object1166295356* super = __this;
    int (*old) (void*, struct class_std_Object1166295356*);
    old = (*(*this).vtable).equals2039369046;
    (*(*this).vtable).equals2039369046 = std_Object_equals270681549;
    int output;
    output = (*(*this).vtable).equals2039369046(__this, other);
    (*(*this).vtable).equals2039369046 = old;
    return output;
}

struct class_std_String1297677838;

struct class_std_String1297677838_vtable {
	int offset;
	int (*hashcode261036375) (void*);
	int (*equals2039369046) (void*, struct class_std_Object1166295356*);
	void (*drop115478603) (void*);
	struct class_std_String1297677838* (*toString1664535608) (void*);
};
struct class_std_String1297677838 {
	struct class_std_String1297677838_vtable* vtable;
	struct class_std_ClassInfo804771843* info;
	long int references;
	struct class_std_ClassInfo804771843* (*getClass2062954782) (void*);
	char* backingPtr;
	int length;
	struct class_std_String1297677838* (*concat1339931399) (void*, struct class_std_String1297677838*);
	struct class_std_String1297677838* (*concat1663899886) (void*, char*);
	const char* (*getCStr36737184) (void*);
};

struct class_std_String1297677838* std_String_concat554257430(void*, struct class_std_String1297677838*);
struct class_std_String1297677838* std_String_concat230288943(void*, char*);
const char* std_String_getCStr1930926013(void*);
int std_Object_hashcode2049014220(void*);
int std_Object_equals270681549(void*, struct class_std_Object1166295356*);
void std_String_drop2009667432(void*);
struct class_std_String1297677838* std_Object_toString320381093(void*);

struct class_std_String1297677838* construct_std_String1918627717_575179913(void*, const char*);
struct class_std_String1297677838* construct_std_String1_22080246(void*);

static struct class_std_String1297677838* class_std_String1297677838_init231149038() {
    struct class_std_String1297677838* output;
    output = calloc(1, sizeof(struct class_std_String1297677838));
    struct class_std_String1297677838_vtable* vtable;
    vtable = malloc(sizeof(struct class_std_String1297677838_vtable));
    (*output).vtable = vtable;
    (*vtable).offset = 0;
    (*vtable).hashcode261036375 = std_Object_hashcode2049014220;
    (*vtable).equals2039369046 = std_Object_equals270681549;
    (*vtable).drop115478603 = std_String_drop2009667432;
    (*vtable).toString1664535608 = std_Object_toString320381093;
    (*output).getClass2062954782 = std_Object_getClass247095813;
    (*output).concat1339931399 = std_String_concat554257430;
    (*output).concat1663899886 = std_String_concat230288943;
    (*output).getCStr36737184 = std_String_getCStr1930926013;
    (*output).info = (struct class_std_ClassInfo804771843*) {0};
    (*output).references = (long int) {0};
    (*output).backingPtr = (char*) {0};
    (*output).length = (int) {0};
    return output;
}





static void super_std_Object_drop21003953041501769405(void* __this) {
    struct class_std_String1297677838* this = __this;
    struct class_std_Object1166295356* super = __this;
    void (*old) (void*);
    old = (*(*this).vtable).drop115478603;
    (*(*this).vtable).drop115478603 = std_Object_drop2100395304;
    (*(*this).vtable).drop115478603(__this);
    (*(*this).vtable).drop115478603 = old;
}

struct class_animals_animal893929093;

struct class_animals_animal893929093_vtable {
	int offset;
	int (*hashcode261036375) (void*);
	int (*equals2039369046) (void*, struct class_std_Object1166295356*);
	void (*drop115478603) (void*);
	struct class_std_String1297677838* (*toString1664535608) (void*);
	void (*says115909444) (void*);
};
struct class_animals_animal893929093 {
	struct class_animals_animal893929093_vtable* vtable;
	struct class_std_ClassInfo804771843* info;
	long int references;
	struct class_std_ClassInfo804771843* (*getClass2062954782) (void*);
	char* species;
	int numberOfLegs;
	int (*getNumberOfLegs1629143273) (void*);
};

int animals_animal_getNumberOfLegs229987604(void*);
int std_Object_hashcode2049014220(void*);
int std_Object_equals270681549(void*, struct class_std_Object1166295356*);
void std_Object_drop2100395304(void*);
struct class_std_String1297677838* std_Object_toString320381093(void*);
void animals_animal_says1975040321(void*);

struct class_animals_animal893929093* construct_animals_animal1909502372_273961694(void*, char*, int);

static struct class_animals_animal893929093* class_animals_animal893929093_init33990921() {
    struct class_animals_animal893929093* output;
    output = calloc(1, sizeof(struct class_animals_animal893929093));
    struct class_animals_animal893929093_vtable* vtable;
    vtable = malloc(sizeof(struct class_animals_animal893929093_vtable));
    (*output).vtable = vtable;
    (*vtable).offset = 0;
    (*vtable).hashcode261036375 = std_Object_hashcode2049014220;
    (*vtable).equals2039369046 = std_Object_equals270681549;
    (*vtable).drop115478603 = std_Object_drop2100395304;
    (*vtable).toString1664535608 = std_Object_toString320381093;
    (*vtable).says115909444 = animals_animal_says1975040321;
    (*output).getClass2062954782 = std_Object_getClass247095813;
    (*output).getNumberOfLegs1629143273 = animals_animal_getNumberOfLegs229987604;
    (*output).info = (struct class_std_ClassInfo804771843*) {0};
    (*output).references = (long int) {0};
    (*output).species = (char*) {0};
    (*output).numberOfLegs = (int) {0};
    return output;
}

int animals_animal_getNumberOfLegs229987604(void* __this) {
    struct class_animals_animal893929093* this = __this;
    struct class_std_Object1166295356* super = __this;
    return (*this).numberOfLegs;
}




struct class_animals_animal893929093* construct_animals_animal1909502372_273961694(void* __this, char* species, int numberOfLegs) {
    struct class_animals_animal893929093* this = (struct class_animals_animal893929093*) __this;
    (*this).species = species;
    (*this).numberOfLegs = numberOfLegs;
    return this;
}

void animals_animal_says1975040321(void* __this) {
    struct class_animals_animal893929093* this = __this;
    struct class_std_Object1166295356* super = __this;
    print((*this).species);
    print();
}


struct class_animals_quadAnimal1667235156;

struct class_animals_quadAnimal1667235156_vtable {
	int offset;
	int (*hashcode261036375) (void*);
	int (*equals2039369046) (void*, struct class_std_Object1166295356*);
	void (*drop115478603) (void*);
	struct class_std_String1297677838* (*toString1664535608) (void*);
	void (*says115909444) (void*);
};
struct class_animals_quadAnimal1667235156 {
	struct class_animals_quadAnimal1667235156_vtable* vtable;
	struct class_std_ClassInfo804771843* info;
	long int references;
	struct class_std_ClassInfo804771843* (*getClass2062954782) (void*);
	char* species;
	int numberOfLegs;
	int (*getNumberOfLegs1629143273) (void*);
};

int std_Object_hashcode2049014220(void*);
int std_Object_equals270681549(void*, struct class_std_Object1166295356*);
void std_Object_drop2100395304(void*);
struct class_std_String1297677838* std_Object_toString320381093(void*);
void animals_quadAnimal_says1622162977(void*);

struct class_animals_quadAnimal1667235156* construct_animals_quadAnimal1792845141_491929247(void*, char*);

static struct class_animals_quadAnimal1667235156* class_animals_quadAnimal1667235156_init177264178() {
    struct class_animals_quadAnimal1667235156* output;
    output = calloc(1, sizeof(struct class_animals_quadAnimal1667235156));
    struct class_animals_quadAnimal1667235156_vtable* vtable;
    vtable = malloc(sizeof(struct class_animals_quadAnimal1667235156_vtable));
    (*output).vtable = vtable;
    (*vtable).offset = 0;
    (*vtable).hashcode261036375 = std_Object_hashcode2049014220;
    (*vtable).equals2039369046 = std_Object_equals270681549;
    (*vtable).drop115478603 = std_Object_drop2100395304;
    (*vtable).toString1664535608 = std_Object_toString320381093;
    (*vtable).says115909444 = animals_quadAnimal_says1622162977;
    (*output).getClass2062954782 = std_Object_getClass247095813;
    (*output).getNumberOfLegs1629143273 = animals_animal_getNumberOfLegs229987604;
    (*output).info = (struct class_std_ClassInfo804771843*) {0};
    (*output).references = (long int) {0};
    (*output).species = (char*) {0};
    (*output).numberOfLegs = (int) {0};
    return output;
}



struct class_animals_quadAnimal1667235156* construct_animals_quadAnimal1792845141_491929247(void* __this, char* name) {
    construct_animals_animal1909502372_273961694(__this, name, 4);
    struct class_animals_quadAnimal1667235156* this = (struct class_animals_quadAnimal1667235156*) __this;
    return this;
}

static void super_animals_animal_says1975040321551565808(void* __this) {
    struct class_animals_quadAnimal1667235156* this = __this;
    struct class_animals_animal893929093* super = __this;
    void (*old) (void*);
    old = (*(*this).vtable).says115909444;
    (*(*this).vtable).says115909444 = animals_animal_says1975040321;
    (*(*this).vtable).says115909444(__this);
    (*(*this).vtable).says115909444 = old;
}

void animals_quadAnimal_says1622162977(void* __this) {
    struct class_animals_quadAnimal1667235156* this = __this;
    struct class_animals_animal893929093* super = __this;
    super_animals_animal_says1975040321551565808(super);
    print();
}


struct class_animals_domesticated140668231;

struct class_animals_domesticated140668231_vtable {
	int offset;
	int (*hashcode261036375) (void*);
	int (*equals2039369046) (void*, struct class_std_Object1166295356*);
	void (*drop115478603) (void*);
	struct class_std_String1297677838* (*toString1664535608) (void*);
	void (*says115909444) (void*);
};
struct class_animals_domesticated140668231 {
	struct class_animals_domesticated140668231_vtable* vtable;
	struct class_std_ClassInfo804771843* info;
	long int references;
	struct class_std_ClassInfo804771843* (*getClass2062954782) (void*);
	char* species;
	int numberOfLegs;
	int (*getNumberOfLegs1629143273) (void*);
	char* name;
	char* (*getName37078109) (void*);
};

char* animals_domesticated_getName1146134310(void*);
int std_Object_hashcode2049014220(void*);
int std_Object_equals270681549(void*, struct class_std_Object1166295356*);
void std_Object_drop2100395304(void*);
struct class_std_String1297677838* std_Object_toString320381093(void*);
void animals_domesticated_says1067302975(void*);

struct class_animals_domesticated140668231* construct_animals_domesticated1580395670_364277771(void*, char*, char*);

static struct class_animals_domesticated140668231* class_animals_domesticated140668231_init1762842846() {
    struct class_animals_domesticated140668231* output;
    output = calloc(1, sizeof(struct class_animals_domesticated140668231));
    struct class_animals_domesticated140668231_vtable* vtable;
    vtable = malloc(sizeof(struct class_animals_domesticated140668231_vtable));
    (*output).vtable = vtable;
    (*vtable).offset = 0;
    (*vtable).hashcode261036375 = std_Object_hashcode2049014220;
    (*vtable).equals2039369046 = std_Object_equals270681549;
    (*vtable).drop115478603 = std_Object_drop2100395304;
    (*vtable).toString1664535608 = std_Object_toString320381093;
    (*vtable).says115909444 = animals_domesticated_says1067302975;
    (*output).getClass2062954782 = std_Object_getClass247095813;
    (*output).getNumberOfLegs1629143273 = animals_animal_getNumberOfLegs229987604;
    (*output).getName37078109 = animals_domesticated_getName1146134310;
    (*output).info = (struct class_std_ClassInfo804771843*) {0};
    (*output).references = (long int) {0};
    (*output).species = (char*) {0};
    (*output).numberOfLegs = (int) {0};
    (*output).name = (char*) {0};
    return output;
}

char* animals_domesticated_getName1146134310(void* __this) {
    struct class_animals_domesticated140668231* this = __this;
    struct class_animals_quadAnimal1667235156* super = __this;
    return (*this).name;
}




struct class_animals_domesticated140668231* construct_animals_domesticated1580395670_364277771(void* __this, char* name, char* species) {
    construct_animals_quadAnimal1792845141_491929247(__this, species);
    struct class_animals_domesticated140668231* this = (struct class_animals_domesticated140668231*) __this;
    (*this).name = name;
    return this;
}

static void super_animals_quadAnimal_says16221629771647351461(void* __this) {
    struct class_animals_domesticated140668231* this = __this;
    struct class_animals_quadAnimal1667235156* super = __this;
    void (*old) (void*);
    old = (*(*this).vtable).says115909444;
    (*(*this).vtable).says115909444 = animals_quadAnimal_says1622162977;
    (*(*this).vtable).says115909444(__this);
    (*(*this).vtable).says115909444 = old;
}

void animals_domesticated_says1067302975(void* __this) {
    struct class_animals_domesticated140668231* this = __this;
    struct class_animals_quadAnimal1667235156* super = __this;
    print((*this).getName37078109(this));
    print();
    super_animals_quadAnimal_says16221629771647351461(super);
    print();
}


struct class_animals_dog1987822139;

struct class_animals_dog1987822139_vtable {
	int offset;
	int (*hashcode261036375) (void*);
	int (*equals2039369046) (void*, struct class_std_Object1166295356*);
	void (*drop115478603) (void*);
	struct class_std_String1297677838* (*toString1664535608) (void*);
	void (*says115909444) (void*);
};
struct class_animals_dog1987822139 {
	struct class_animals_dog1987822139_vtable* vtable;
	struct class_std_ClassInfo804771843* info;
	long int references;
	struct class_std_ClassInfo804771843* (*getClass2062954782) (void*);
	char* species;
	int numberOfLegs;
	int (*getNumberOfLegs1629143273) (void*);
	char* name;
	char* (*getName37078109) (void*);
};

int std_Object_hashcode2049014220(void*);
int std_Object_equals270681549(void*, struct class_std_Object1166295356*);
void std_Object_drop2100395304(void*);
struct class_std_String1297677838* std_Object_toString320381093(void*);
void animals_domesticated_says1067302975(void*);

struct class_animals_dog1987822139* construct_animals_dog627185362_1113340593(void*, char*);
struct class_animals_dog1987822139* construct_animals_dog1_1572717144(void*);

static struct class_animals_dog1987822139* class_animals_dog1987822139_init1886552747() {
    struct class_animals_dog1987822139* output;
    output = calloc(1, sizeof(struct class_animals_dog1987822139));
    struct class_animals_dog1987822139_vtable* vtable;
    vtable = malloc(sizeof(struct class_animals_dog1987822139_vtable));
    (*output).vtable = vtable;
    (*vtable).offset = 0;
    (*vtable).hashcode261036375 = std_Object_hashcode2049014220;
    (*vtable).equals2039369046 = std_Object_equals270681549;
    (*vtable).drop115478603 = std_Object_drop2100395304;
    (*vtable).toString1664535608 = std_Object_toString320381093;
    (*vtable).says115909444 = animals_domesticated_says1067302975;
    (*output).getClass2062954782 = std_Object_getClass247095813;
    (*output).getNumberOfLegs1629143273 = animals_animal_getNumberOfLegs229987604;
    (*output).getName37078109 = animals_domesticated_getName1146134310;
    (*output).info = (struct class_std_ClassInfo804771843*) {0};
    (*output).references = (long int) {0};
    (*output).species = (char*) {0};
    (*output).numberOfLegs = (int) {0};
    (*output).name = (char*) {0};
    return output;
}



struct class_animals_dog1987822139* construct_animals_dog627185362_1113340593(void* __this, char* name) {
    construct_animals_domesticated1580395670_364277771(__this, name, );
    struct class_animals_dog1987822139* this = (struct class_animals_dog1987822139*) __this;
    return this;
}


struct class_animals_dog1987822139* construct_animals_dog1_1572717144(void* __this) {
    construct_animals_dog627185362_1113340593(__this, );
    struct class_animals_dog1987822139* this = (struct class_animals_dog1987822139*) __this;
    return this;
}

struct class_cat976698061;

struct class_cat976698061_vtable {
	int offset;
	int (*hashcode261036375) (void*);
	int (*equals2039369046) (void*, struct class_std_Object1166295356*);
	void (*drop115478603) (void*);
	struct class_std_String1297677838* (*toString1664535608) (void*);
	void (*says115909444) (void*);
};
struct class_cat976698061 {
	struct class_cat976698061_vtable* vtable;
	struct class_std_ClassInfo804771843* info;
	long int references;
	struct class_std_ClassInfo804771843* (*getClass2062954782) (void*);
	char* species;
	int numberOfLegs;
	int (*getNumberOfLegs1629143273) (void*);
	char* name;
	char* (*getName37078109) (void*);
};

int std_Object_hashcode2049014220(void*);
int std_Object_equals270681549(void*, struct class_std_Object1166295356*);
void std_Object_drop2100395304(void*);
struct class_std_String1297677838* std_Object_toString320381093(void*);
void cat_says2050453759(void*);

struct class_cat976698061* construct_cat706277979_665430571(void*, char*);

static struct class_cat976698061* class_cat976698061_init674116585() {
    struct class_cat976698061* output;
    output = calloc(1, sizeof(struct class_cat976698061));
    struct class_cat976698061_vtable* vtable;
    vtable = malloc(sizeof(struct class_cat976698061_vtable));
    (*output).vtable = vtable;
    (*vtable).offset = 0;
    (*vtable).hashcode261036375 = std_Object_hashcode2049014220;
    (*vtable).equals2039369046 = std_Object_equals270681549;
    (*vtable).drop115478603 = std_Object_drop2100395304;
    (*vtable).toString1664535608 = std_Object_toString320381093;
    (*vtable).says115909444 = cat_says2050453759;
    (*output).getClass2062954782 = std_Object_getClass247095813;
    (*output).getNumberOfLegs1629143273 = animals_animal_getNumberOfLegs229987604;
    (*output).getName37078109 = animals_domesticated_getName1146134310;
    (*output).info = (struct class_std_ClassInfo804771843*) {0};
    (*output).references = (long int) {0};
    (*output).species = (char*) {0};
    (*output).numberOfLegs = (int) {0};
    (*output).name = (char*) {0};
    return output;
}



struct class_cat976698061* construct_cat706277979_665430571(void* __this, char* name) {
    construct_animals_domesticated1580395670_364277771(__this, name, );
    struct class_cat976698061* this = (struct class_cat976698061*) __this;
    return this;
}

static void super_animals_domesticated_says10673029752124510951(void* __this) {
    struct class_cat976698061* this = __this;
    struct class_animals_domesticated140668231* super = __this;
    void (*old) (void*);
    old = (*(*this).vtable).says115909444;
    (*(*this).vtable).says115909444 = animals_domesticated_says1067302975;
    (*(*this).vtable).says115909444(__this);
    (*(*this).vtable).says115909444 = old;
}

void cat_says2050453759(void* __this) {
    struct class_cat976698061* this = __this;
    struct class_animals_domesticated140668231* super = __this;
    println();
}


