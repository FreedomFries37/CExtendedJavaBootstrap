struct class_std_ClassInfo947057859;
struct class_std_String1439963854;
typedef unsigned long int class_id;
void* malloc(unsigned int);
void* calloc(unsigned int, unsigned int);
void free(void*);
void print(const char*);
void println(const char*);
void print_s(struct class_std_String1439963854*);
void println_s(struct class_std_String1439963854*);
struct class_std_ClassInfo947057859* getClass(class_id);
struct class_std_Object1308581372;

struct class_std_Object1308581372_vtable {
	int offset;
	int (*hashcode261036375) (void*);
	int (*equals1427009906) (void*, struct class_std_Object1308581372*);
	void (*drop115478603) (void*);
	struct class_std_String1439963854* (*toString1664535608) (void*);
};
struct class_std_Object1308581372 {
	struct class_std_Object1308581372_vtable* vtable;
	struct class_std_ClassInfo947057859* info;
	long int references;
	struct class_std_ClassInfo947057859* (*getClass2062954782) (void*);
};

struct class_std_ClassInfo947057859* std_Object_getClass1223495393(void*);
int std_Object_hashcode578423014(void*);
int std_Object_equals587550517(void*, struct class_std_Object1308581372*);
void std_Object_drop723980786(void*);
struct class_std_String1439963854* std_Object_toString1790972299(void*);

struct class_std_Object1308581372* construct_std_Object1_503284340(void*);

static struct class_std_Object1308581372* class_std_Object1308581372_init2038729822() {
    struct class_std_Object1308581372* output;
    output = calloc(1, sizeof(struct class_std_Object1308581372));
    struct class_std_Object1308581372_vtable* vtable;
    vtable = malloc(sizeof(struct class_std_Object1308581372_vtable));
    (*output).vtable = vtable;
    (*vtable).offset = 0;
    (*vtable).hashcode261036375 = std_Object_hashcode578423014;
    (*vtable).equals1427009906 = std_Object_equals587550517;
    (*vtable).drop115478603 = std_Object_drop723980786;
    (*vtable).toString1664535608 = std_Object_toString1790972299;
    (*output).getClass2062954782 = std_Object_getClass1223495393;
    (*output).info = (struct class_std_ClassInfo947057859*) {0};
    (*output).references = (long int) {0};
    return output;
}



struct class_std_ClassInfo947057859;

struct class_std_ClassInfo947057859_vtable {
	int offset;
	int (*hashcode261036375) (void*);
	int (*equals1427009906) (void*, struct class_std_Object1308581372*);
	void (*drop115478603) (void*);
	struct class_std_String1439963854* (*toString1664535608) (void*);
	int (*equals619456805) (void*, struct class_std_ClassInfo947057859*);
};
struct class_std_ClassInfo947057859 {
	struct class_std_ClassInfo947057859_vtable* vtable;
	struct class_std_ClassInfo947057859* info;
	long int references;
	struct class_std_ClassInfo947057859* (*getClass2062954782) (void*);
	struct class_std_String1439963854* name;
	struct class_std_ClassInfo947057859* parent;
	int classHash;
	struct class_std_String1439963854* (*getName37078109) (void*);
	int (*is1572471075) (void*, struct class_std_Object1308581372*);
};

struct class_std_String1439963854* std_ClassInfo_getName513768192(void*);
int std_ClassInfo_is1095780992(void*, struct class_std_Object1308581372*);
int std_Object_hashcode578423014(void*);
int std_ClassInfo_equals1903699989(void*, struct class_std_Object1308581372*);
void std_Object_drop723980786(void*);
struct class_std_String1439963854* std_Object_toString1790972299(void*);
int std_ClassInfo_equals142766722(void*, struct class_std_ClassInfo947057859*);

struct class_std_ClassInfo947057859* construct_std_ClassInfo1700100987_970825678(void*, struct class_std_String1439963854*, struct class_std_ClassInfo947057859*, int);

static struct class_std_ClassInfo947057859* class_std_ClassInfo947057859_init686314421() {
    struct class_std_ClassInfo947057859* output;
    output = calloc(1, sizeof(struct class_std_ClassInfo947057859));
    struct class_std_ClassInfo947057859_vtable* vtable;
    vtable = malloc(sizeof(struct class_std_ClassInfo947057859_vtable));
    (*output).vtable = vtable;
    (*vtable).offset = 0;
    (*vtable).hashcode261036375 = std_Object_hashcode578423014;
    (*vtable).equals1427009906 = std_ClassInfo_equals1903699989;
    (*vtable).drop115478603 = std_Object_drop723980786;
    (*vtable).toString1664535608 = std_Object_toString1790972299;
    (*vtable).equals619456805 = std_ClassInfo_equals142766722;
    (*output).getClass2062954782 = std_Object_getClass1223495393;
    (*output).getName37078109 = std_ClassInfo_getName513768192;
    (*output).is1572471075 = std_ClassInfo_is1095780992;
    (*output).info = output;
    (*output).references = (long int) {0};
    (*output).name = (struct class_std_String1439963854*) {0};
    (*output).parent = (struct class_std_ClassInfo947057859*) {0};
    (*output).classHash = (int) {0};
    return output;
}




static int super_std_Object_equals587550517492843943(void* __this, struct class_std_Object1308581372* other) {
    struct class_std_ClassInfo947057859* this = __this;
    struct class_std_Object1308581372* super = __this;
    int (*old) (void*, struct class_std_Object1308581372*);
    old = (*(*this).vtable).equals1427009906;
    (*(*this).vtable).equals1427009906 = std_Object_equals587550517;
    int output;
    output = (*(*this).vtable).equals1427009906(__this, other);
    (*(*this).vtable).equals1427009906 = old;
    return output;
}

struct class_std_String1439963854;

struct class_std_String1439963854_vtable {
	int offset;
	int (*hashcode261036375) (void*);
	int (*equals1427009906) (void*, struct class_std_Object1308581372*);
	void (*drop115478603) (void*);
	struct class_std_String1439963854* (*toString1664535608) (void*);
};
struct class_std_String1439963854 {
	struct class_std_String1439963854_vtable* vtable;
	struct class_std_ClassInfo947057859* info;
	long int references;
	struct class_std_ClassInfo947057859* (*getClass2062954782) (void*);
	char* backingPtr;
	int length;
	struct class_std_String1439963854* (*concat1952290539) (void*, struct class_std_String1439963854*);
	struct class_std_String1439963854* (*concat1663899886) (void*, char*);
	const char* (*getCStr36737184) (void*);
};

struct class_std_String1439963854* std_String_concat1412489496(void*, struct class_std_String1439963854*);
struct class_std_String1439963854* std_String_concat1700880149(void*, char*);
const char* std_String_getCStr893450077(void*);
int std_Object_hashcode578423014(void*);
int std_Object_equals587550517(void*, struct class_std_Object1308581372*);
void std_String_drop814708658(void*);
struct class_std_String1439963854* std_Object_toString1790972299(void*);

struct class_std_String1439963854* construct_std_String105704998_1986898245(void*, const char*);
struct class_std_String1439963854* construct_std_String1_1332611760(void*);

static struct class_std_String1439963854* class_std_String1439963854_init1852022000() {
    struct class_std_String1439963854* output;
    output = calloc(1, sizeof(struct class_std_String1439963854));
    struct class_std_String1439963854_vtable* vtable;
    vtable = malloc(sizeof(struct class_std_String1439963854_vtable));
    (*output).vtable = vtable;
    (*vtable).offset = 0;
    (*vtable).hashcode261036375 = std_Object_hashcode578423014;
    (*vtable).equals1427009906 = std_Object_equals587550517;
    (*vtable).drop115478603 = std_String_drop814708658;
    (*vtable).toString1664535608 = std_Object_toString1790972299;
    (*output).getClass2062954782 = std_Object_getClass1223495393;
    (*output).concat1952290539 = std_String_concat1412489496;
    (*output).concat1663899886 = std_String_concat1700880149;
    (*output).getCStr36737184 = std_String_getCStr893450077;
    (*output).info = (struct class_std_ClassInfo947057859*) {0};
    (*output).references = (long int) {0};
    (*output).backingPtr = (char*) {0};
    (*output).length = (int) {0};
    return output;
}





static void super_std_Object_drop723980786884941318(void* __this) {
    struct class_std_String1439963854* this = __this;
    struct class_std_Object1308581372* super = __this;
    void (*old) (void*);
    old = (*(*this).vtable).drop115478603;
    (*(*this).vtable).drop115478603 = std_Object_drop723980786;
    (*(*this).vtable).drop115478603(__this);
    (*(*this).vtable).drop115478603 = old;
}

struct class_animals_animal1036215109;

struct class_animals_animal1036215109_vtable {
	int offset;
	int (*hashcode261036375) (void*);
	int (*equals1427009906) (void*, struct class_std_Object1308581372*);
	void (*drop115478603) (void*);
	struct class_std_String1439963854* (*toString1664535608) (void*);
	void (*says115909444) (void*);
};
struct class_animals_animal1036215109 {
	struct class_animals_animal1036215109_vtable* vtable;
	struct class_std_ClassInfo947057859* info;
	long int references;
	struct class_std_ClassInfo947057859* (*getClass2062954782) (void*);
	char* species;
	int numberOfLegs;
	int (*getNumberOfLegs1629143273) (void*);
};

int animals_animal_getNumberOfLegs1700578810(void*);
int std_Object_hashcode578423014(void*);
int std_Object_equals587550517(void*, struct class_std_Object1308581372*);
void std_Object_drop723980786(void*);
struct class_std_String1439963854* std_Object_toString1790972299(void*);
void animals_animal_says849335769(void*);

struct class_animals_animal1036215109* construct_animals_animal576950815_1562001960(void*, char*, int);

static struct class_animals_animal1036215109* class_animals_animal1036215109_init1171575971() {
    struct class_animals_animal1036215109* output;
    output = calloc(1, sizeof(struct class_animals_animal1036215109));
    struct class_animals_animal1036215109_vtable* vtable;
    vtable = malloc(sizeof(struct class_animals_animal1036215109_vtable));
    (*output).vtable = vtable;
    (*vtable).offset = 0;
    (*vtable).hashcode261036375 = std_Object_hashcode578423014;
    (*vtable).equals1427009906 = std_Object_equals587550517;
    (*vtable).drop115478603 = std_Object_drop723980786;
    (*vtable).toString1664535608 = std_Object_toString1790972299;
    (*vtable).says115909444 = animals_animal_says849335769;
    (*output).getClass2062954782 = std_Object_getClass1223495393;
    (*output).getNumberOfLegs1629143273 = animals_animal_getNumberOfLegs1700578810;
    (*output).info = (struct class_std_ClassInfo947057859*) {0};
    (*output).references = (long int) {0};
    (*output).species = (char*) {0};
    (*output).numberOfLegs = (int) {0};
    return output;
}

int animals_animal_getNumberOfLegs1700578810(void* __this) {
    struct class_animals_animal1036215109* this = __this;
    struct class_std_Object1308581372* super = __this;
    return (*this).numberOfLegs;
}




struct class_animals_animal1036215109* construct_animals_animal576950815_1562001960(void* __this, char* species, int numberOfLegs) {
    struct class_animals_animal1036215109* this = (struct class_animals_animal1036215109*) __this;
    (*this).species = species;
    (*this).numberOfLegs = numberOfLegs;
    return this;
}

void animals_animal_says849335769(void* __this) {
    struct class_animals_animal1036215109* this = __this;
    struct class_std_Object1308581372* super = __this;
    print((*this).species);
    print();
}


struct class_animals_quadAnimal1524949140;

struct class_animals_quadAnimal1524949140_vtable {
	int offset;
	int (*hashcode261036375) (void*);
	int (*equals1427009906) (void*, struct class_std_Object1308581372*);
	void (*drop115478603) (void*);
	struct class_std_String1439963854* (*toString1664535608) (void*);
	void (*says115909444) (void*);
};
struct class_animals_quadAnimal1524949140 {
	struct class_animals_quadAnimal1524949140_vtable* vtable;
	struct class_std_ClassInfo947057859* info;
	long int references;
	struct class_std_ClassInfo947057859* (*getClass2062954782) (void*);
	char* species;
	int numberOfLegs;
	int (*getNumberOfLegs1629143273) (void*);
};

int std_Object_hashcode578423014(void*);
int std_Object_equals587550517(void*, struct class_std_Object1308581372*);
void std_Object_drop723980786(void*);
struct class_std_String1439963854* std_Object_toString1790972299(void*);
void animals_quadAnimal_says1202213113(void*);

struct class_animals_quadAnimal1524949140* construct_animals_quadAnimal1709537787_1854469613(void*, char*);

static struct class_animals_quadAnimal1524949140* class_animals_quadAnimal1524949140_init1202807609() {
    struct class_animals_quadAnimal1524949140* output;
    output = calloc(1, sizeof(struct class_animals_quadAnimal1524949140));
    struct class_animals_quadAnimal1524949140_vtable* vtable;
    vtable = malloc(sizeof(struct class_animals_quadAnimal1524949140_vtable));
    (*output).vtable = vtable;
    (*vtable).offset = 0;
    (*vtable).hashcode261036375 = std_Object_hashcode578423014;
    (*vtable).equals1427009906 = std_Object_equals587550517;
    (*vtable).drop115478603 = std_Object_drop723980786;
    (*vtable).toString1664535608 = std_Object_toString1790972299;
    (*vtable).says115909444 = animals_quadAnimal_says1202213113;
    (*output).getClass2062954782 = std_Object_getClass1223495393;
    (*output).getNumberOfLegs1629143273 = animals_animal_getNumberOfLegs1700578810;
    (*output).info = (struct class_std_ClassInfo947057859*) {0};
    (*output).references = (long int) {0};
    (*output).species = (char*) {0};
    (*output).numberOfLegs = (int) {0};
    return output;
}



struct class_animals_quadAnimal1524949140* construct_animals_quadAnimal1709537787_1854469613(void* __this, char* name) {
    construct_animals_animal576950815_1562001960(__this, name, 4);
    struct class_animals_quadAnimal1524949140* this = (struct class_animals_quadAnimal1524949140*) __this;
    return this;
}

static void super_animals_animal_says849335769474570478(void* __this) {
    struct class_animals_quadAnimal1524949140* this = __this;
    struct class_animals_animal1036215109* super = __this;
    void (*old) (void*);
    old = (*(*this).vtable).says115909444;
    (*(*this).vtable).says115909444 = animals_animal_says849335769;
    (*(*this).vtable).says115909444(__this);
    (*(*this).vtable).says115909444 = old;
}

void animals_quadAnimal_says1202213113(void* __this) {
    struct class_animals_quadAnimal1524949140* this = __this;
    struct class_animals_animal1036215109* super = __this;
    super_animals_animal_says849335769474570478(super);
    print();
}


struct class_animals_domesticated1617785;

struct class_animals_domesticated1617785_vtable {
	int offset;
	int (*hashcode261036375) (void*);
	int (*equals1427009906) (void*, struct class_std_Object1308581372*);
	void (*drop115478603) (void*);
	struct class_std_String1439963854* (*toString1664535608) (void*);
	void (*says115909444) (void*);
};
struct class_animals_domesticated1617785 {
	struct class_animals_domesticated1617785_vtable* vtable;
	struct class_std_ClassInfo947057859* info;
	long int references;
	struct class_std_ClassInfo947057859* (*getClass2062954782) (void*);
	char* species;
	int numberOfLegs;
	int (*getNumberOfLegs1629143273) (void*);
	char* name;
	char* (*getName37078109) (void*);
};

char* animals_domesticated_getName324456896(void*);
int std_Object_hashcode578423014(void*);
int std_Object_equals587550517(void*, struct class_std_Object1308581372*);
void std_Object_drop723980786(void*);
struct class_std_String1439963854* std_Object_toString1790972299(void*);
void animals_domesticated_says403288231(void*);

struct class_animals_domesticated1617785* construct_animals_domesticated526333424_1226216923(void*, char*, char*);

static struct class_animals_domesticated1617785* class_animals_domesticated1617785_init1393384964() {
    struct class_animals_domesticated1617785* output;
    output = calloc(1, sizeof(struct class_animals_domesticated1617785));
    struct class_animals_domesticated1617785_vtable* vtable;
    vtable = malloc(sizeof(struct class_animals_domesticated1617785_vtable));
    (*output).vtable = vtable;
    (*vtable).offset = 0;
    (*vtable).hashcode261036375 = std_Object_hashcode578423014;
    (*vtable).equals1427009906 = std_Object_equals587550517;
    (*vtable).drop115478603 = std_Object_drop723980786;
    (*vtable).toString1664535608 = std_Object_toString1790972299;
    (*vtable).says115909444 = animals_domesticated_says403288231;
    (*output).getClass2062954782 = std_Object_getClass1223495393;
    (*output).getNumberOfLegs1629143273 = animals_animal_getNumberOfLegs1700578810;
    (*output).getName37078109 = animals_domesticated_getName324456896;
    (*output).info = (struct class_std_ClassInfo947057859*) {0};
    (*output).references = (long int) {0};
    (*output).species = (char*) {0};
    (*output).numberOfLegs = (int) {0};
    (*output).name = (char*) {0};
    return output;
}

char* animals_domesticated_getName324456896(void* __this) {
    struct class_animals_domesticated1617785* this = __this;
    struct class_animals_quadAnimal1524949140* super = __this;
    return (*this).name;
}




struct class_animals_domesticated1617785* construct_animals_domesticated526333424_1226216923(void* __this, char* name, char* species) {
    construct_animals_quadAnimal1709537787_1854469613(__this, species);
    struct class_animals_domesticated1617785* this = (struct class_animals_domesticated1617785*) __this;
    (*this).name = name;
    return this;
}

static void super_animals_quadAnimal_says1202213113819396110(void* __this) {
    struct class_animals_domesticated1617785* this = __this;
    struct class_animals_quadAnimal1524949140* super = __this;
    void (*old) (void*);
    old = (*(*this).vtable).says115909444;
    (*(*this).vtable).says115909444 = animals_quadAnimal_says1202213113;
    (*(*this).vtable).says115909444(__this);
    (*(*this).vtable).says115909444 = old;
}

void animals_domesticated_says403288231(void* __this) {
    struct class_animals_domesticated1617785* this = __this;
    struct class_animals_quadAnimal1524949140* super = __this;
    print((*this).getName37078109(this));
    print();
    super_animals_quadAnimal_says1202213113819396110(super);
    print();
}


struct class_animals_dog1845536123;

struct class_animals_dog1845536123_vtable {
	int offset;
	int (*hashcode261036375) (void*);
	int (*equals1427009906) (void*, struct class_std_Object1308581372*);
	void (*drop115478603) (void*);
	struct class_std_String1439963854* (*toString1664535608) (void*);
	void (*says115909444) (void*);
};
struct class_animals_dog1845536123 {
	struct class_animals_dog1845536123_vtable* vtable;
	struct class_std_ClassInfo947057859* info;
	long int references;
	struct class_std_ClassInfo947057859* (*getClass2062954782) (void*);
	char* species;
	int numberOfLegs;
	int (*getNumberOfLegs1629143273) (void*);
	char* name;
	char* (*getName37078109) (void*);
};

int std_Object_hashcode578423014(void*);
int std_Object_equals587550517(void*, struct class_std_Object1308581372*);
void std_Object_drop723980786(void*);
struct class_std_String1439963854* std_Object_toString1790972299(void*);
void animals_domesticated_says403288231(void*);

struct class_animals_dog1845536123* construct_animals_dog124313308_1863205912(void*, char*);
struct class_animals_dog1845536123* construct_animals_dog1_1367558146(void*);

static struct class_animals_dog1845536123* class_animals_dog1845536123_init1947069887() {
    struct class_animals_dog1845536123* output;
    output = calloc(1, sizeof(struct class_animals_dog1845536123));
    struct class_animals_dog1845536123_vtable* vtable;
    vtable = malloc(sizeof(struct class_animals_dog1845536123_vtable));
    (*output).vtable = vtable;
    (*vtable).offset = 0;
    (*vtable).hashcode261036375 = std_Object_hashcode578423014;
    (*vtable).equals1427009906 = std_Object_equals587550517;
    (*vtable).drop115478603 = std_Object_drop723980786;
    (*vtable).toString1664535608 = std_Object_toString1790972299;
    (*vtable).says115909444 = animals_domesticated_says403288231;
    (*output).getClass2062954782 = std_Object_getClass1223495393;
    (*output).getNumberOfLegs1629143273 = animals_animal_getNumberOfLegs1700578810;
    (*output).getName37078109 = animals_domesticated_getName324456896;
    (*output).info = (struct class_std_ClassInfo947057859*) {0};
    (*output).references = (long int) {0};
    (*output).species = (char*) {0};
    (*output).numberOfLegs = (int) {0};
    (*output).name = (char*) {0};
    return output;
}



struct class_animals_dog1845536123* construct_animals_dog124313308_1863205912(void* __this, char* name) {
    construct_animals_domesticated526333424_1226216923(__this, name, );
    struct class_animals_dog1845536123* this = (struct class_animals_dog1845536123*) __this;
    return this;
}


struct class_animals_dog1845536123* construct_animals_dog1_1367558146(void* __this) {
    construct_animals_dog124313308_1863205912(__this, );
    struct class_animals_dog1845536123* this = (struct class_animals_dog1845536123*) __this;
    return this;
}

struct class_cat235707929;

struct class_cat235707929_vtable {
	int offset;
	int (*hashcode261036375) (void*);
	int (*equals1427009906) (void*, struct class_std_Object1308581372*);
	void (*drop115478603) (void*);
	struct class_std_String1439963854* (*toString1664535608) (void*);
	void (*says115909444) (void*);
};
struct class_cat235707929 {
	struct class_cat235707929_vtable* vtable;
	struct class_std_ClassInfo947057859* info;
	long int references;
	struct class_std_ClassInfo947057859* (*getClass2062954782) (void*);
	char* species;
	int numberOfLegs;
	int (*getNumberOfLegs1629143273) (void*);
	char* name;
	char* (*getName37078109) (void*);
};

int std_Object_hashcode578423014(void*);
int std_Object_equals587550517(void*, struct class_std_Object1308581372*);
void std_Object_drop723980786(void*);
struct class_std_String1439963854* std_Object_toString1790972299(void*);
void cat_says695761753(void*);

struct class_cat235707929* construct_cat1225616436_1555224437(void*, char*);

static struct class_cat235707929* class_cat235707929_init861438073() {
    struct class_cat235707929* output;
    output = calloc(1, sizeof(struct class_cat235707929));
    struct class_cat235707929_vtable* vtable;
    vtable = malloc(sizeof(struct class_cat235707929_vtable));
    (*output).vtable = vtable;
    (*vtable).offset = 0;
    (*vtable).hashcode261036375 = std_Object_hashcode578423014;
    (*vtable).equals1427009906 = std_Object_equals587550517;
    (*vtable).drop115478603 = std_Object_drop723980786;
    (*vtable).toString1664535608 = std_Object_toString1790972299;
    (*vtable).says115909444 = cat_says695761753;
    (*output).getClass2062954782 = std_Object_getClass1223495393;
    (*output).getNumberOfLegs1629143273 = animals_animal_getNumberOfLegs1700578810;
    (*output).getName37078109 = animals_domesticated_getName324456896;
    (*output).info = (struct class_std_ClassInfo947057859*) {0};
    (*output).references = (long int) {0};
    (*output).species = (char*) {0};
    (*output).numberOfLegs = (int) {0};
    (*output).name = (char*) {0};
    return output;
}



struct class_cat235707929* construct_cat1225616436_1555224437(void* __this, char* name) {
    construct_animals_domesticated526333424_1226216923(__this, name, );
    struct class_cat235707929* this = (struct class_cat235707929*) __this;
    return this;
}

static void super_animals_domesticated_says4032882311772616098(void* __this) {
    struct class_cat235707929* this = __this;
    struct class_animals_domesticated1617785* super = __this;
    void (*old) (void*);
    old = (*(*this).vtable).says115909444;
    (*(*this).vtable).says115909444 = animals_domesticated_says403288231;
    (*(*this).vtable).says115909444(__this);
    (*(*this).vtable).says115909444 = old;
}

void cat_says695761753(void* __this) {
    struct class_cat235707929* this = __this;
    struct class_animals_domesticated1617785* super = __this;
    println();
}


