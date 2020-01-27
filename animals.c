struct class_std_ClassInfo1758136957;
struct class_std_String1265230962;
typedef unsigned long int class_id;
void* malloc(unsigned int);
void* calloc(unsigned int, unsigned int);
void free(void*);
void print(const char*);
void println(const char*);
void print_s(struct class_std_String1265230962*);
void println_s(struct class_std_String1265230962*);
struct class_std_ClassInfo1758136957* getClass(class_id);
struct class_std_Object1396613444;

struct class_std_Object1396613444_vtable {
	int offset;
	int (*hashcode261036375) (void*);
	int (*equals456858111) (void*, struct class_std_Object1396613444*);
	void (*drop115478603) (void*);
	struct class_std_String1265230962* (*toString1664535608) (void*);
};
struct class_std_Object1396613444 {
	struct class_std_Object1396613444_vtable* vtable;
	struct class_std_ClassInfo1758136957* info;
	long int references;
	struct class_std_ClassInfo1758136957* (*getClass2062954782) (void*);
};

struct class_std_ClassInfo1758136957* std_Object_getClass2078066969(void*);
int std_Object_hashcode414981920(void*);
int std_Object_equals302912566(void*, struct class_std_Object1396613444*);
void std_Object_drop269424148(void*);
struct class_std_String1265230962* std_Object_toString1510590063(void*);

struct class_std_Object1396613444* construct_std_Object1_1548186030(void*);

static struct class_std_Object1396613444* class_std_Object1396613444_init413997239() {
    struct class_std_Object1396613444* output;
    output = calloc(1, sizeof(struct class_std_Object1396613444));
    struct class_std_Object1396613444_vtable* vtable;
    vtable = malloc(sizeof(struct class_std_Object1396613444_vtable));
    (*output).vtable = vtable;
    (*vtable).offset = 0;
    (*vtable).hashcode261036375 = std_Object_hashcode414981920;
    (*vtable).equals456858111 = std_Object_equals302912566;
    (*vtable).drop115478603 = std_Object_drop269424148;
    (*vtable).toString1664535608 = std_Object_toString1510590063;
    (*output).getClass2062954782 = std_Object_getClass2078066969;
    (*output).info = (struct class_std_ClassInfo1758136957*) {0};
    (*output).references = (long int) {0};
    return output;
}



struct class_std_ClassInfo1758136957;

struct class_std_ClassInfo1758136957_vtable {
	int offset;
	int (*hashcode261036375) (void*);
	int (*equals456858111) (void*, struct class_std_Object1396613444*);
	void (*drop115478603) (void*);
	struct class_std_String1265230962* (*toString1664535608) (void*);
	int (*equals93884397) (void*, struct class_std_ClassInfo1758136957*);
};
struct class_std_ClassInfo1758136957 {
	struct class_std_ClassInfo1758136957_vtable* vtable;
	struct class_std_ClassInfo1758136957* info;
	long int references;
	struct class_std_ClassInfo1758136957* (*getClass2062954782) (void*);
	struct class_std_String1265230962* name;
	struct class_std_ClassInfo1758136957* parent;
	int classHash;
	struct class_std_String1265230962* (*getName37078109) (void*);
	int (*is838628204) (void*, struct class_std_Object1396613444*);
};

struct class_std_String1265230962* std_ClassInfo_getName1507173126(void*);
int std_ClassInfo_is1986244075(void*, struct class_std_Object1396613444*);
int std_Object_hashcode414981920(void*);
int std_ClassInfo_equals1013236906(void*, struct class_std_Object1396613444*);
void std_Object_drop269424148(void*);
struct class_std_String1265230962* std_Object_toString1510590063(void*);
int std_ClassInfo_equals1563979414(void*, struct class_std_ClassInfo1758136957*);

struct class_std_ClassInfo1758136957* construct_std_ClassInfo2125116918_275029387(void*, struct class_std_String1265230962*, struct class_std_ClassInfo1758136957*, int);

static struct class_std_ClassInfo1758136957* class_std_ClassInfo1758136957_init1907262049() {
    struct class_std_ClassInfo1758136957* output;
    output = calloc(1, sizeof(struct class_std_ClassInfo1758136957));
    struct class_std_ClassInfo1758136957_vtable* vtable;
    vtable = malloc(sizeof(struct class_std_ClassInfo1758136957_vtable));
    (*output).vtable = vtable;
    (*vtable).offset = 0;
    (*vtable).hashcode261036375 = std_Object_hashcode414981920;
    (*vtable).equals456858111 = std_ClassInfo_equals1013236906;
    (*vtable).drop115478603 = std_Object_drop269424148;
    (*vtable).toString1664535608 = std_Object_toString1510590063;
    (*vtable).equals93884397 = std_ClassInfo_equals1563979414;
    (*output).getClass2062954782 = std_Object_getClass2078066969;
    (*output).getName37078109 = std_ClassInfo_getName1507173126;
    (*output).is838628204 = std_ClassInfo_is1986244075;
    (*output).info = output;
    (*output).references = (long int) {0};
    (*output).name = (struct class_std_String1265230962*) {0};
    (*output).parent = (struct class_std_ClassInfo1758136957*) {0};
    (*output).classHash = (int) {0};
    return output;
}




static int super_std_Object_equals302912566895954369(void* __this, struct class_std_Object1396613444* other) {
    struct class_std_ClassInfo1758136957* this = __this;
    struct class_std_Object1396613444* super = __this;
    int (*old) (void*, struct class_std_Object1396613444*);
    old = (*(*this).vtable).equals456858111;
    (*(*this).vtable).equals456858111 = std_Object_equals302912566;
    int output;
    output = (*(*this).vtable).equals456858111(__this, other);
    (*(*this).vtable).equals456858111 = old;
    return output;
}

struct class_std_String1265230962;

struct class_std_String1265230962_vtable {
	int offset;
	int (*hashcode261036375) (void*);
	int (*equals456858111) (void*, struct class_std_Object1396613444*);
	void (*drop115478603) (void*);
	struct class_std_String1265230962* (*toString1664535608) (void*);
};
struct class_std_String1265230962 {
	struct class_std_String1265230962_vtable* vtable;
	struct class_std_ClassInfo1758136957* info;
	long int references;
	struct class_std_ClassInfo1758136957* (*getClass2062954782) (void*);
	char* backingPtr;
	int length;
	struct class_std_String1265230962* (*concat1220815129) (void*, struct class_std_String1265230962*);
	struct class_std_String1265230962* (*concat1663899886) (void*, char*);
	const char* (*getCStr36737184) (void*);
};

struct class_std_String1265230962* std_String_concat1284032802(void*, struct class_std_String1265230962*);
struct class_std_String1265230962* std_String_concat1600682213(void*, char*);
const char* std_String_getCStr99954857(void*);
int std_Object_hashcode414981920(void*);
int std_Object_equals302912566(void*, struct class_std_Object1396613444*);
void std_String_drop178696276(void*);
struct class_std_String1265230962* std_Object_toString1510590063(void*);

struct class_std_String1265230962* construct_std_String1464642142_1940998136(void*, const char*);
struct class_std_String1265230962* construct_std_String1_287710070(void*);

static struct class_std_String1265230962* class_std_String1265230962_init1479292166() {
    struct class_std_String1265230962* output;
    output = calloc(1, sizeof(struct class_std_String1265230962));
    struct class_std_String1265230962_vtable* vtable;
    vtable = malloc(sizeof(struct class_std_String1265230962_vtable));
    (*output).vtable = vtable;
    (*vtable).offset = 0;
    (*vtable).hashcode261036375 = std_Object_hashcode414981920;
    (*vtable).equals456858111 = std_Object_equals302912566;
    (*vtable).drop115478603 = std_String_drop178696276;
    (*vtable).toString1664535608 = std_Object_toString1510590063;
    (*output).getClass2062954782 = std_Object_getClass2078066969;
    (*output).concat1220815129 = std_String_concat1284032802;
    (*output).concat1663899886 = std_String_concat1600682213;
    (*output).getCStr36737184 = std_String_getCStr99954857;
    (*output).info = (struct class_std_ClassInfo1758136957*) {0};
    (*output).references = (long int) {0};
    (*output).backingPtr = (char*) {0};
    (*output).length = (int) {0};
    return output;
}





static void super_std_Object_drop2694241482075249944(void* __this) {
    struct class_std_String1265230962* this = __this;
    struct class_std_Object1396613444* super = __this;
    void (*old) (void*);
    old = (*(*this).vtable).drop115478603;
    (*(*this).vtable).drop115478603 = std_Object_drop269424148;
    (*(*this).vtable).drop115478603(__this);
    (*(*this).vtable).drop115478603 = old;
}

struct class_animals_animal1668979707;

struct class_animals_animal1668979707_vtable {
	int offset;
	int (*hashcode261036375) (void*);
	int (*equals456858111) (void*, struct class_std_Object1396613444*);
	void (*drop115478603) (void*);
	struct class_std_String1265230962* (*toString1664535608) (void*);
	void (*says115909444) (void*);
};
struct class_animals_animal1668979707 {
	struct class_animals_animal1668979707_vtable* vtable;
	struct class_std_ClassInfo1758136957* info;
	long int references;
	struct class_std_ClassInfo1758136957* (*getClass2062954782) (void*);
	char* species;
	int numberOfLegs;
	int (*getNumberOfLegs1629143273) (void*);
};

int animals_animal_getNumberOfLegs1600983552(void*);
int std_Object_hashcode414981920(void*);
int std_Object_equals302912566(void*, struct class_std_Object1396613444*);
void std_Object_drop269424148(void*);
struct class_std_String1265230962* std_Object_toString1510590063(void*);
void animals_animal_says144069165(void*);

struct class_animals_animal1668979707* construct_animals_animal1118839280_1360429503(void*, char*, int);

static struct class_animals_animal1668979707* class_animals_animal1668979707_init869730977() {
    struct class_animals_animal1668979707* output;
    output = calloc(1, sizeof(struct class_animals_animal1668979707));
    struct class_animals_animal1668979707_vtable* vtable;
    vtable = malloc(sizeof(struct class_animals_animal1668979707_vtable));
    (*output).vtable = vtable;
    (*vtable).offset = 0;
    (*vtable).hashcode261036375 = std_Object_hashcode414981920;
    (*vtable).equals456858111 = std_Object_equals302912566;
    (*vtable).drop115478603 = std_Object_drop269424148;
    (*vtable).toString1664535608 = std_Object_toString1510590063;
    (*vtable).says115909444 = animals_animal_says144069165;
    (*output).getClass2062954782 = std_Object_getClass2078066969;
    (*output).getNumberOfLegs1629143273 = animals_animal_getNumberOfLegs1600983552;
    (*output).info = (struct class_std_ClassInfo1758136957*) {0};
    (*output).references = (long int) {0};
    (*output).species = (char*) {0};
    (*output).numberOfLegs = (int) {0};
    return output;
}

int animals_animal_getNumberOfLegs1600983552(void* __this) {
    struct class_animals_animal1668979707* this = __this;
    struct class_std_Object1396613444* super = __this;
    return (*this).numberOfLegs;
}




struct class_animals_animal1668979707* construct_animals_animal1118839280_1360429503(void* __this, char* species, int numberOfLegs) {
    struct class_animals_animal1668979707* this = (struct class_animals_animal1668979707*) __this;
    (*this).species = species;
    (*this).numberOfLegs = numberOfLegs;
    return this;
}

void animals_animal_says144069165(void* __this) {
    struct class_animals_animal1668979707* this = __this;
    struct class_std_Object1396613444* super = __this;
    print((*this).species);
    print();
}


struct class_animals_quadAnimal64823340;

struct class_animals_quadAnimal64823340_vtable {
	int offset;
	int (*hashcode261036375) (void*);
	int (*equals456858111) (void*, struct class_std_Object1396613444*);
	void (*drop115478603) (void*);
	struct class_std_String1265230962* (*toString1664535608) (void*);
	void (*says115909444) (void*);
};
struct class_animals_quadAnimal64823340 {
	struct class_animals_quadAnimal64823340_vtable* vtable;
	struct class_std_ClassInfo1758136957* info;
	long int references;
	struct class_std_ClassInfo1758136957* (*getClass2062954782) (void*);
	char* species;
	int numberOfLegs;
	int (*getNumberOfLegs1629143273) (void*);
};

int std_Object_hashcode414981920(void*);
int std_Object_equals302912566(void*, struct class_std_Object1396613444*);
void std_Object_drop269424148(void*);
struct class_std_String1265230962* std_Object_toString1510590063(void*);
void animals_quadAnimal_says208808179(void*);

struct class_animals_quadAnimal64823340* construct_animals_quadAnimal1286084990_718177668(void*, char*);

static struct class_animals_quadAnimal64823340* class_animals_quadAnimal64823340_init845049682() {
    struct class_animals_quadAnimal64823340* output;
    output = calloc(1, sizeof(struct class_animals_quadAnimal64823340));
    struct class_animals_quadAnimal64823340_vtable* vtable;
    vtable = malloc(sizeof(struct class_animals_quadAnimal64823340_vtable));
    (*output).vtable = vtable;
    (*vtable).offset = 0;
    (*vtable).hashcode261036375 = std_Object_hashcode414981920;
    (*vtable).equals456858111 = std_Object_equals302912566;
    (*vtable).drop115478603 = std_Object_drop269424148;
    (*vtable).toString1664535608 = std_Object_toString1510590063;
    (*vtable).says115909444 = animals_quadAnimal_says208808179;
    (*output).getClass2062954782 = std_Object_getClass2078066969;
    (*output).getNumberOfLegs1629143273 = animals_animal_getNumberOfLegs1600983552;
    (*output).info = (struct class_std_ClassInfo1758136957*) {0};
    (*output).references = (long int) {0};
    (*output).species = (char*) {0};
    (*output).numberOfLegs = (int) {0};
    return output;
}



struct class_animals_quadAnimal64823340* construct_animals_quadAnimal1286084990_718177668(void* __this, char* name) {
    construct_animals_animal1118839280_1360429503(__this, name, 4);
    struct class_animals_quadAnimal64823340* this = (struct class_animals_quadAnimal64823340*) __this;
    return this;
}

static void super_animals_animal_says1440691651028606464(void* __this) {
    struct class_animals_quadAnimal64823340* this = __this;
    struct class_animals_animal1668979707* super = __this;
    void (*old) (void*);
    old = (*(*this).vtable).says115909444;
    (*(*this).vtable).says115909444 = animals_animal_says144069165;
    (*(*this).vtable).says115909444(__this);
    (*(*this).vtable).says115909444 = old;
}

void animals_quadAnimal_says208808179(void* __this) {
    struct class_animals_quadAnimal64823340* this = __this;
    struct class_animals_animal1668979707* super = __this;
    super_animals_animal_says1440691651028606464(super);
    print();
}


struct class_animals_domesticated1591390265;

struct class_animals_domesticated1591390265_vtable {
	int offset;
	int (*hashcode261036375) (void*);
	int (*equals456858111) (void*, struct class_std_Object1396613444*);
	void (*drop115478603) (void*);
	struct class_std_String1265230962* (*toString1664535608) (void*);
	void (*says115909444) (void*);
};
struct class_animals_domesticated1591390265 {
	struct class_animals_domesticated1591390265_vtable* vtable;
	struct class_std_ClassInfo1758136957* info;
	long int references;
	struct class_std_ClassInfo1758136957* (*getClass2062954782) (void*);
	char* species;
	int numberOfLegs;
	int (*getNumberOfLegs1629143273) (void*);
	char* name;
	char* (*getName37078109) (void*);
};

char* animals_domesticated_getName1317861830(void*);
int std_Object_hashcode414981920(void*);
int std_Object_equals302912566(void*, struct class_std_Object1396613444*);
void std_Object_drop269424148(void*);
struct class_std_String1265230962* std_Object_toString1510590063(void*);
void animals_domesticated_says1396693165(void*);

struct class_animals_domesticated1591390265* construct_animals_domesticated1199280647_803750126(void*, char*, char*);

static struct class_animals_domesticated1591390265* class_animals_domesticated1591390265_init2132259538() {
    struct class_animals_domesticated1591390265* output;
    output = calloc(1, sizeof(struct class_animals_domesticated1591390265));
    struct class_animals_domesticated1591390265_vtable* vtable;
    vtable = malloc(sizeof(struct class_animals_domesticated1591390265_vtable));
    (*output).vtable = vtable;
    (*vtable).offset = 0;
    (*vtable).hashcode261036375 = std_Object_hashcode414981920;
    (*vtable).equals456858111 = std_Object_equals302912566;
    (*vtable).drop115478603 = std_Object_drop269424148;
    (*vtable).toString1664535608 = std_Object_toString1510590063;
    (*vtable).says115909444 = animals_domesticated_says1396693165;
    (*output).getClass2062954782 = std_Object_getClass2078066969;
    (*output).getNumberOfLegs1629143273 = animals_animal_getNumberOfLegs1600983552;
    (*output).getName37078109 = animals_domesticated_getName1317861830;
    (*output).info = (struct class_std_ClassInfo1758136957*) {0};
    (*output).references = (long int) {0};
    (*output).species = (char*) {0};
    (*output).numberOfLegs = (int) {0};
    (*output).name = (char*) {0};
    return output;
}

char* animals_domesticated_getName1317861830(void* __this) {
    struct class_animals_domesticated1591390265* this = __this;
    struct class_animals_quadAnimal64823340* super = __this;
    return (*this).name;
}




struct class_animals_domesticated1591390265* construct_animals_domesticated1199280647_803750126(void* __this, char* name, char* species) {
    construct_animals_quadAnimal1286084990_718177668(__this, species);
    struct class_animals_domesticated1591390265* this = (struct class_animals_domesticated1591390265*) __this;
    (*this).name = name;
    return this;
}

static void super_animals_quadAnimal_says2088081791665402829(void* __this) {
    struct class_animals_domesticated1591390265* this = __this;
    struct class_animals_quadAnimal64823340* super = __this;
    void (*old) (void*);
    old = (*(*this).vtable).says115909444;
    (*(*this).vtable).says115909444 = animals_quadAnimal_says208808179;
    (*(*this).vtable).says115909444(__this);
    (*(*this).vtable).says115909444 = old;
}

void animals_domesticated_says1396693165(void* __this) {
    struct class_animals_domesticated1591390265* this = __this;
    struct class_animals_quadAnimal64823340* super = __this;
    print((*this).getName37078109(this));
    print();
    super_animals_quadAnimal_says2088081791665402829(super);
    print();
}


struct class_animals_dog255763643;

struct class_animals_dog255763643_vtable {
	int offset;
	int (*hashcode261036375) (void*);
	int (*equals456858111) (void*, struct class_std_Object1396613444*);
	void (*drop115478603) (void*);
	struct class_std_String1265230962* (*toString1664535608) (void*);
	void (*says115909444) (void*);
};
struct class_animals_dog255763643 {
	struct class_animals_dog255763643_vtable* vtable;
	struct class_std_ClassInfo1758136957* info;
	long int references;
	struct class_std_ClassInfo1758136957* (*getClass2062954782) (void*);
	char* species;
	int numberOfLegs;
	int (*getNumberOfLegs1629143273) (void*);
	char* name;
	char* (*getName37078109) (void*);
};

int std_Object_hashcode414981920(void*);
int std_Object_equals302912566(void*, struct class_std_Object1396613444*);
void std_Object_drop269424148(void*);
struct class_std_String1265230962* std_Object_toString1510590063(void*);
void animals_domesticated_says1396693165(void*);

struct class_animals_dog255763643* construct_animals_dog184966274_260540044(void*, char*);
struct class_animals_dog255763643* construct_animals_dog1_1882507460(void*);

static struct class_animals_dog255763643* class_animals_dog255763643_init1542774514() {
    struct class_animals_dog255763643* output;
    output = calloc(1, sizeof(struct class_animals_dog255763643));
    struct class_animals_dog255763643_vtable* vtable;
    vtable = malloc(sizeof(struct class_animals_dog255763643_vtable));
    (*output).vtable = vtable;
    (*vtable).offset = 0;
    (*vtable).hashcode261036375 = std_Object_hashcode414981920;
    (*vtable).equals456858111 = std_Object_equals302912566;
    (*vtable).drop115478603 = std_Object_drop269424148;
    (*vtable).toString1664535608 = std_Object_toString1510590063;
    (*vtable).says115909444 = animals_domesticated_says1396693165;
    (*output).getClass2062954782 = std_Object_getClass2078066969;
    (*output).getNumberOfLegs1629143273 = animals_animal_getNumberOfLegs1600983552;
    (*output).getName37078109 = animals_domesticated_getName1317861830;
    (*output).info = (struct class_std_ClassInfo1758136957*) {0};
    (*output).references = (long int) {0};
    (*output).species = (char*) {0};
    (*output).numberOfLegs = (int) {0};
    (*output).name = (char*) {0};
    return output;
}



struct class_animals_dog255763643* construct_animals_dog184966274_260540044(void* __this, char* name) {
    construct_animals_domesticated1199280647_803750126(__this, name, );
    struct class_animals_dog255763643* this = (struct class_animals_dog255763643*) __this;
    return this;
}


struct class_animals_dog255763643* construct_animals_dog1_1882507460(void* __this) {
    construct_animals_dog184966274_260540044(__this, );
    struct class_animals_dog255763643* this = (struct class_animals_dog255763643*) __this;
    return this;
}

struct class_cat1896001055;

struct class_cat1896001055_vtable {
	int offset;
	int (*hashcode261036375) (void*);
	int (*equals456858111) (void*, struct class_std_Object1396613444*);
	void (*drop115478603) (void*);
	struct class_std_String1265230962* (*toString1664535608) (void*);
	void (*says115909444) (void*);
};
struct class_cat1896001055 {
	struct class_cat1896001055_vtable* vtable;
	struct class_std_ClassInfo1758136957* info;
	long int references;
	struct class_std_ClassInfo1758136957* (*getClass2062954782) (void*);
	char* species;
	int numberOfLegs;
	int (*getNumberOfLegs1629143273) (void*);
	char* name;
	char* (*getName37078109) (void*);
};

int std_Object_hashcode414981920(void*);
int std_Object_equals302912566(void*, struct class_std_Object1396613444*);
void std_Object_drop269424148(void*);
struct class_std_String1265230962* std_Object_toString1510590063(void*);
void cat_says1740663443(void*);

struct class_cat1896001055* construct_cat124313308_129631718(void*, char*);

static struct class_cat1896001055* class_cat1896001055_init1125320578() {
    struct class_cat1896001055* output;
    output = calloc(1, sizeof(struct class_cat1896001055));
    struct class_cat1896001055_vtable* vtable;
    vtable = malloc(sizeof(struct class_cat1896001055_vtable));
    (*output).vtable = vtable;
    (*vtable).offset = 0;
    (*vtable).hashcode261036375 = std_Object_hashcode414981920;
    (*vtable).equals456858111 = std_Object_equals302912566;
    (*vtable).drop115478603 = std_Object_drop269424148;
    (*vtable).toString1664535608 = std_Object_toString1510590063;
    (*vtable).says115909444 = cat_says1740663443;
    (*output).getClass2062954782 = std_Object_getClass2078066969;
    (*output).getNumberOfLegs1629143273 = animals_animal_getNumberOfLegs1600983552;
    (*output).getName37078109 = animals_domesticated_getName1317861830;
    (*output).info = (struct class_std_ClassInfo1758136957*) {0};
    (*output).references = (long int) {0};
    (*output).species = (char*) {0};
    (*output).numberOfLegs = (int) {0};
    (*output).name = (char*) {0};
    return output;
}



struct class_cat1896001055* construct_cat124313308_129631718(void* __this, char* name) {
    construct_animals_domesticated1199280647_803750126(__this, name, );
    struct class_cat1896001055* this = (struct class_cat1896001055*) __this;
    return this;
}

static void super_animals_domesticated_says13966931651661294866(void* __this) {
    struct class_cat1896001055* this = __this;
    struct class_animals_domesticated1591390265* super = __this;
    void (*old) (void*);
    old = (*(*this).vtable).says115909444;
    (*(*this).vtable).says115909444 = animals_domesticated_says1396693165;
    (*(*this).vtable).says115909444(__this);
    (*(*this).vtable).says115909444 = old;
}

void cat_says1740663443(void* __this) {
    struct class_cat1896001055* this = __this;
    struct class_animals_domesticated1591390265* super = __this;
    println();
}


