#include <stdlib.h>
void print(char* name);
void println(char* name);


void print(char*);
void println(char*);
struct class_animals_animal2086640357_vtable {
	int offset;
	void (*says115909444) (void*);
};
struct class_animals_animal2086640357 {
	struct class_animals_animal2086640357_vtable* vtable;
	char* species;
	int numberOfLegs;
	int (*getNumberOfLegs1629143273) (void*);
};

int animals_animal_getNumberOfLegs718838711(void*);
void animals_animal_says1831075868(void*);

struct class_animals_animal2086640357* construct_animals_animal1223983060_2117108529(void*, char*, int);

static struct class_animals_animal2086640357* class_animals_animal2086640357_init830776319() {
    struct class_animals_animal2086640357* output;
    output = calloc(1, sizeof(struct class_animals_animal2086640357));
    struct class_animals_animal2086640357_vtable* vtable;
    vtable = malloc(sizeof(struct class_animals_animal2086640357_vtable));
    (*output).vtable = vtable;
    (*vtable).offset = 0;
    (*vtable).says115909444 = animals_animal_says1831075868;
    (*output).getNumberOfLegs1629143273 = animals_animal_getNumberOfLegs718838711;
    return output;
}

int animals_animal_getNumberOfLegs718838711(void* __this) {
    struct class_animals_animal2086640357* this;
    this = (struct class_animals_animal2086640357*) __this;
    return (*this).numberOfLegs;
}




struct class_animals_animal2086640357* construct_animals_animal1223983060_2117108529(void* __this, char* species, int numberOfLegs) {
    struct class_animals_animal2086640357* this = (struct class_animals_animal2086640357*) __this;
    (*this).species = species;
    (*this).numberOfLegs = numberOfLegs;
    return this;
}

void animals_animal_says1831075868(void* __this) {
    struct class_animals_animal2086640357* this;
    this = (struct class_animals_animal2086640357*) __this;
    print((*this).species);
    print(" says ");
}


struct class_animals_quadAnimal474523892_vtable {
	int offset;
	void (*says115909444) (void*);
};
struct class_animals_quadAnimal474523892 {
	struct class_animals_quadAnimal474523892_vtable* vtable;
	char* species;
	int numberOfLegs;
	int (*getNumberOfLegs1629143273) (void*);
};

void animals_quadAnimal_says2111014084(void*);

struct class_animals_quadAnimal474523892* construct_animals_quadAnimal836514746_632576832(void*, char*);

static struct class_animals_quadAnimal474523892* class_animals_quadAnimal474523892_init1344579889() {
    struct class_animals_quadAnimal474523892* output;
    output = calloc(1, sizeof(struct class_animals_quadAnimal474523892));
    struct class_animals_quadAnimal474523892_vtable* vtable;
    vtable = malloc(sizeof(struct class_animals_quadAnimal474523892_vtable));
    (*output).vtable = vtable;
    (*vtable).offset = 0;
    (*vtable).says115909444 = animals_quadAnimal_says2111014084;
    (*output).getNumberOfLegs1629143273 = animals_animal_getNumberOfLegs718838711;
    return output;
}



struct class_animals_quadAnimal474523892* construct_animals_quadAnimal836514746_632576832(void* __this, char* name) {
    construct_animals_animal1223983060_2117108529(__this, name, 4);
    struct class_animals_quadAnimal474523892* this = (struct class_animals_quadAnimal474523892*) __this;
    return this;
}

static void super_animals_animal_says18310758681694836456(void* __this) {
    struct class_animals_quadAnimal474523892* this;
    this = (struct class_animals_quadAnimal474523892*) __this;
    struct class_animals_quadAnimal474523892* super;
    super = this;
    void (*old) (void*);
    old = (*(*this).vtable).says115909444;
    (*(*this).vtable).says115909444 = animals_animal_says1831075868;
    (*(*this).vtable).says115909444(__this);
    (*(*this).vtable).says115909444 = old;
}

void animals_quadAnimal_says2111014084(void* __this) {
    struct class_animals_quadAnimal474523892* this;
    this = (struct class_animals_quadAnimal474523892*) __this;
    struct class_animals_quadAnimal474523892* super;
    super = this;
    super_animals_animal_says18310758681694836456(super);
    print("I have 4 legs!");
}


struct class_animals_domesticated1052043033_vtable {
	int offset;
	void (*says115909444) (void*);
};
struct class_animals_domesticated1052043033 {
	struct class_animals_domesticated1052043033_vtable* vtable;
	char* species;
	int numberOfLegs;
	int (*getNumberOfLegs1629143273) (void*);
	char* name;
	char* (*getName37078109) (void*);
};

char* animals_domesticated_getName657283203(void*);
void animals_domesticated_says578451868(void*);

struct class_animals_domesticated1052043033* construct_animals_domesticated1728949239_1320088878(void*, char*, char*);

static struct class_animals_domesticated1052043033* class_animals_domesticated1052043033_init1187487185() {
    struct class_animals_domesticated1052043033* output;
    output = calloc(1, sizeof(struct class_animals_domesticated1052043033));
    struct class_animals_domesticated1052043033_vtable* vtable;
    vtable = malloc(sizeof(struct class_animals_domesticated1052043033_vtable));
    (*output).vtable = vtable;
    (*vtable).offset = 0;
    (*vtable).says115909444 = animals_domesticated_says578451868;
    (*output).getNumberOfLegs1629143273 = animals_animal_getNumberOfLegs718838711;
    (*output).getName37078109 = animals_domesticated_getName657283203;
    return output;
}

char* animals_domesticated_getName657283203(void* __this) {
    struct class_animals_domesticated1052043033* this;
    this = (struct class_animals_domesticated1052043033*) __this;
    struct class_animals_domesticated1052043033* super;
    super = this;
    return (*this).name;
}




struct class_animals_domesticated1052043033* construct_animals_domesticated1728949239_1320088878(void* __this, char* name, char* species) {
    construct_animals_quadAnimal836514746_632576832(__this, species);
    struct class_animals_domesticated1052043033* this = (struct class_animals_domesticated1052043033*) __this;
    (*this).name = name;
    return this;
}

static void super_animals_quadAnimal_says21110140841890529467(void* __this) {
    struct class_animals_domesticated1052043033* this;
    this = (struct class_animals_domesticated1052043033*) __this;
    struct class_animals_domesticated1052043033* super;
    super = this;
    void (*old) (void*);
    old = (*(*this).vtable).says115909444;
    (*(*this).vtable).says115909444 = animals_quadAnimal_says2111014084;
    (*(*this).vtable).says115909444(__this);
    (*(*this).vtable).says115909444 = old;
}

void animals_domesticated_says578451868(void* __this) {
    struct class_animals_domesticated1052043033* this;
    this = (struct class_animals_domesticated1052043033*) __this;
    struct class_animals_domesticated1052043033* super;
    super = this;
    print((*this).getName37078109(this));
    print(" the ");
    super_animals_quadAnimal_says21110140841890529467(super);
    print(", also ARF");
}


struct class_animals_dog795110875_vtable {
	int offset;
	void (*says115909444) (void*);
};
struct class_animals_dog795110875 {
	struct class_animals_dog795110875_vtable* vtable;
	char* species;
	int numberOfLegs;
	int (*getNumberOfLegs1629143273) (void*);
	char* name;
	char* (*getName37078109) (void*);
};

void animals_domesticated_says578451868(void*);

struct class_animals_dog795110875* construct_animals_dog1899073251_351408243(void*, char*);
struct class_animals_dog795110875* construct_animals_dog1_552742565(void*);

static struct class_animals_dog795110875* class_animals_dog795110875_init927922561() {
    struct class_animals_dog795110875* output;
    output = calloc(1, sizeof(struct class_animals_dog795110875));
    struct class_animals_dog795110875_vtable* vtable;
    vtable = malloc(sizeof(struct class_animals_dog795110875_vtable));
    (*output).vtable = vtable;
    (*vtable).offset = 0;
    (*vtable).says115909444 = animals_domesticated_says578451868;
    (*output).getNumberOfLegs1629143273 = animals_animal_getNumberOfLegs718838711;
    (*output).getName37078109 = animals_domesticated_getName657283203;
    return output;
}



struct class_animals_dog795110875* construct_animals_dog1899073251_351408243(void* __this, char* name) {
    construct_animals_domesticated1728949239_1320088878(__this, name, "dog");
    struct class_animals_dog795110875* this = (struct class_animals_dog795110875*) __this;
    return this;
}


struct class_animals_dog795110875* construct_animals_dog1_552742565(void* __this) {
    construct_animals_dog1899073251_351408243(__this, "unknown");
    struct class_animals_dog795110875* this = (struct class_animals_dog795110875*) __this;
    return this;
}


struct class_cat98262_vtable {
	int offset;
	void (*says115909444) (void*);
};
struct class_cat98262 {
	struct class_cat98262_vtable* vtable;
	char* species;
	int numberOfLegs;
	int (*getNumberOfLegs1629143273) (void*);
	char* name;
	char* (*getName37078109) (void*);
};

void cat_says119053828(void*);

struct class_cat98262* construct_cat858242370_255719862(void*, char*);

static struct class_cat98262* class_cat98262_init803422325() {
    struct class_cat98262* output;
    output = calloc(1, sizeof(struct class_cat98262));
    struct class_cat98262_vtable* vtable;
    vtable = malloc(sizeof(struct class_cat98262_vtable));
    (*output).vtable = vtable;
    (*vtable).offset = 0;
    (*vtable).says115909444 = cat_says119053828;
    (*output).getNumberOfLegs1629143273 = animals_animal_getNumberOfLegs718838711;
    (*output).getName37078109 = animals_domesticated_getName657283203;
    return output;
}



struct class_cat98262* construct_cat858242370_255719862(void* __this, char* name) {
    construct_animals_domesticated1728949239_1320088878(__this, name, "cat");
    struct class_cat98262* this = (struct class_cat98262*) __this;
    return this;
}

static void super_animals_domesticated_says578451868994451065(void* __this) {
    struct class_cat98262* this;
    this = (struct class_cat98262*) __this;
    struct class_cat98262* super;
    super = this;
    void (*old) (void*);
    old = (*(*this).vtable).says115909444;
    (*(*this).vtable).says115909444 = animals_domesticated_says578451868;
    (*(*this).vtable).says115909444(__this);
    (*(*this).vtable).says115909444 = old;
}

void cat_says119053828(void* __this) {
    struct class_cat98262* this;
    this = (struct class_cat98262*) __this;
    struct class_cat98262* super;
    super = this;
    println("I'm a cat, shove off");
}


int main() {
    struct class_animals_animal2086640357* griff = construct_animals_dog1899073251_351408243(class_animals_dog795110875_init927922561(), "The Griff");
    (*griff).vtable->says115909444(griff);
    println("");
    (*griff).vtable->says115909444(griff);
    println("");
    struct class_animals_animal2086640357* animals[2];
    struct class_animals_domesticated1052043033* myCat = construct_cat858242370_255719862(class_cat98262_init803422325(), "jeff");
    (*myCat).vtable->says115909444(myCat);
    animals[0] = myCat;
    animals[1] = griff;
    (*animals[0]).vtable->says115909444(animals[0]);
    struct class_animals_dog795110875 dogs[2];
    dogs[0] = (*(struct class_animals_dog795110875*) griff);
    dogs[0].vtable->says115909444(&dogs[0]);
    struct class_animals_animal2086640357 infoLoss = (*griff);
    infoLoss.vtable->says115909444(&infoLoss);
    struct class_animals_animal2086640357* array2D[4][6];
    array2D[2][1] = griff;
    (*array2D[2][1]).vtable->says115909444(array2D[2][1]);
    struct class_animals_dog795110875* typeSafety = (struct class_animals_dog795110875*) &(infoLoss);
    (*typeSafety).vtable->says115909444(typeSafety);
    return 0;
}

