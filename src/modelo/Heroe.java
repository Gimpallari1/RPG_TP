package modelo;

public abstract class Heroe extends Personaje {
    protected int experiencia;
    protected int expSiguienteNivel;
    protected Equipable armaEquipada;
    protected Equipable armaduraEquipada;
    protected Equipable accesorioEquipado;

    public Heroe(String nombre, int hp, int mp, int ataque, int defensa, int velocidad, String spritePath) {
        super(nombre, 1, hp, mp, ataque, defensa, velocidad, spritePath);
        this.experiencia = 0;
        this.expSiguienteNivel = 100;
    }

    public Equipable equipar(Equipable nuevo) {
        Equipable anterior = null;
        switch (nuevo.getRanura()) {
            case ARMA:
                anterior = armaEquipada;
                armaEquipada = nuevo;
                break;
            case ARMADURA:
                anterior = armaduraEquipada;
                armaduraEquipada = nuevo;
                break;
            case ACCESORIO:
                anterior = accesorioEquipado;
                accesorioEquipado = nuevo;
                break;
        }
        return anterior;
    }

    @Override
    public int getAtaqueTotal() {
        int total = super.getAtaqueTotal();
        if (armaEquipada != null) total += armaEquipada.getBonusAtaque();
        if (armaduraEquipada != null) total += armaduraEquipada.getBonusAtaque();
        if (accesorioEquipado != null) total += accesorioEquipado.getBonusAtaque();
        return total;
    }

    @Override
    public int getDefensaTotal() {
        int base = defensaBase;
        if (armaEquipada != null) base += armaEquipada.getBonusDefensa();
        if (armaduraEquipada != null) base += armaduraEquipada.getBonusDefensa();
        if (accesorioEquipado != null) base += accesorioEquipado.getBonusDefensa();
        return defendiendo ? base * 2 : base;
    }

    public int getVelocidadTotal() {
        int vel = velocidad;
        if (armaEquipada != null) vel += armaEquipada.getBonusVelocidad();
        if (armaduraEquipada != null) vel += armaduraEquipada.getBonusVelocidad();
        if (accesorioEquipado != null) vel += accesorioEquipado.getBonusVelocidad();
        return vel;
    }

    @Override
    public int getVelocidadEfectiva() {
        return getVelocidadTotal();
    }

    @Override
    public int compareTo(Personaje otro) {
        int cmpBarra = Double.compare(otro.barraTurno, this.barraTurno);
        if (cmpBarra != 0) return cmpBarra;
        return Integer.compare(otro.getVelocidadEfectiva(), this.getVelocidadEfectiva());
    }

    public void revivir(int porcentajeHp) {
        if (!estaVivo()) {
            this.hpActual = Math.max(1, (this.hpMax * porcentajeHp) / 100);
        }
    }

    public boolean ganarExperiencia(int exp) {
        if (!estaVivo()) return false;
        this.experiencia += exp;
        boolean subio = false;
        while (this.experiencia >= this.expSiguienteNivel) {
            this.experiencia -= this.expSiguienteNivel;
            subirNivel();
            subio = true;
        }
        return subio;
    }

    protected void subirNivel() {
        this.nivel++;
        this.expSiguienteNivel = (int) (this.expSiguienteNivel * 1.4);
        this.hpMax += 20;
        this.hpActual = this.hpMax;
        this.mpMax += 10;
        this.mpActual = this.mpMax;
        this.ataqueBase += 4;
        this.defensaBase += 2;
        this.velocidad += 2;
    }

    public void descansoCompleto() {
        this.hpActual = this.hpMax;
        this.mpActual = this.mpMax;
        this.defendiendo = false;
        this.barraTurno = 0.0;
    }

    public Equipable getArmaEquipada() { return armaEquipada; }
    public Equipable getArmaduraEquipada() { return armaduraEquipada; }
    public Equipable getAccesorioEquipado() { return accesorioEquipado; }
}