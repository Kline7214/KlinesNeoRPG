package net.kline72.ksnrpgmod.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public class PlayerStats {
    private int mana = 25;
    private final int MIN_MANA = 0;
    private int maxMana = 25;
    private int exp = 0;
    private final int MIN_EXP = 0;
    private int maxExp = 50;
    private int maxStamina = 20;
    private final int MIN_STAMINA = 0;
    private int strength = 1;
    private int vitality = 1;
    private int intelligence = 1;
    private int perception = 1;
    private int agility = 1;
    private int spirit = 1;
    private double critChance = (int) 0.001;
    private double critDmg = (int) 0.05;
    private double magicResist = (int) 0.1;
    private double magicDmg = (int) 0.2;
    private int playerLevel = 1;
    private int ula = 0;
    private int attPoints = 0;
    private boolean initialized = false;

    public int getCurrentStamina(Player player) {
        int foodLevel = player.getFoodData().getFoodLevel();
        return Math.max(MIN_STAMINA, Math.min(foodLevel, maxStamina));
    }
    public int getMana() {
        return mana;
    }
    public int getMaxMana() {
        return maxMana;
    }
    public int getExp() {
        return exp;
    }
    public int getMaxExp() {
        return maxExp;
    }
    public int getMaxStamina() {
        return maxStamina;
    }
    public int getStrength() {
        return strength;
    }
    public int getVitality() {
        return vitality;
    }
    public int getIntelligence() {
        return intelligence;
    }
    public int getPerception() {
        return perception;
    }
    public int getAgility() {
        return agility;
    }
    public int getSpirit() {
        return spirit;
    }
    public double getCritChance() {
        return critChance;
    }
    public double getCritDmg() {
        return critDmg;
    }
    public double getMagicResist() {
        return magicResist;
    }
    public double getMagicDmg() {
        return magicDmg;
    }
    public int getPlayerLevel() {
        return playerLevel;
    }
    public int getUla() {
        return ula;
    }
    public int getAttPoints() {
        return attPoints;
    }

    // Add stats
    public void addMana(int add) {
        this.mana = Math.min(mana + add, maxMana);
    }
    public void addExp(int add) {
        this.exp = Math.min(exp + add, maxExp);
    }

    // Subtract Stats
    public void subMana(int sub) {
        this.mana = Math.max(mana + sub, MIN_MANA);
    }
    public void subExp(int sub) {
        this.exp = Math.max(exp - sub, MIN_EXP);
    }

    // Boolean
    public boolean isInitialized() {
        return initialized;
    }

    public void copyFrom(PlayerStats source) {
        this.mana = source.mana;
        this.maxMana = source.maxMana;
        this.exp = source.exp;
        this.maxExp = source.maxExp;
        this.maxStamina = source.maxStamina;
        this.strength = source.strength;
        this.vitality = source.vitality;
        this.intelligence = source.intelligence;
        this.perception = source.perception;
        this.agility = source.agility;
        this.spirit = source.spirit;
        this.critChance = source.critChance;
        this.critDmg = source.critDmg;
        this.magicResist = source.magicResist;
        this.magicDmg = source.magicDmg;
        this.playerLevel = source.playerLevel;
        this.ula = source.ula;
        this.attPoints = source.attPoints;
    }

    public void saveNBTData(CompoundTag nbt) {
        nbt.putInt("mana", mana);
        nbt.putInt("maxMana", maxMana);
        nbt.putInt("exp", exp);
        nbt.putInt("maxExp", maxExp);
        nbt.putInt("maxStamina", maxStamina);
        nbt.putInt("strength", strength);
        nbt.putInt("vitality", vitality);
        nbt.putInt("intelligence", intelligence);
        nbt.putInt("perception", perception);
        nbt.putInt("agility", agility);
        nbt.putInt("spirit", spirit);
        nbt.putDouble("critChance", critChance);
        nbt.putDouble("critDmg", critDmg);
        nbt.putDouble("magicResist", magicResist);
        nbt.putDouble("magicDmg", magicDmg);
        nbt.putInt("playerLevel", playerLevel);
        nbt.putInt("ula", ula);
        nbt.putInt("attPoints", attPoints);
        nbt.putBoolean("initialized", initialized);
    }

    public void loadNBTData(CompoundTag nbt) {
        mana = nbt.getInt("mana");
        maxMana = nbt.getInt("maxMana");
        exp = nbt.getInt("exp");
        maxExp = nbt.getInt("maxExp");
        maxStamina = nbt.getInt("maxStamina");
        strength = nbt.getInt("strength");
        vitality = nbt.getInt("vitality");
        intelligence = nbt.getInt("intelligence");
        perception = nbt.getInt("perception");
        agility = nbt.getInt("agility");
        spirit = nbt.getInt("spirit");
        critChance = nbt.getDouble("critChance");
        critDmg = nbt.getDouble("critDmg");
        magicResist = nbt.getDouble("magicResist");
        magicDmg = nbt.getDouble("magicDmg");
        playerLevel = nbt.getInt("playerLevel");
        ula = nbt.getInt("ula");
        attPoints = nbt.getInt("attPoints");
        initialized = nbt.getBoolean("initialized");
    }

    public void setMana(int mana) {
        this.mana = mana;
    }
    public void setMaxMana(int maxMana) {
        this.maxMana = maxMana;
    }
    public void setExp(int exp) {
        this.exp = exp;
    }
    public void setMaxExp(int maxExp) {
        this.maxExp = maxExp;
    }
    public void setMaxStamina(int maxStamina) {
        this.maxStamina = Math.max(MIN_STAMINA, maxStamina);
    }
    public void setStrength(int strength) {
        this.strength = strength;
    }
    public void setVitality(int vitality) {
        this.vitality = vitality;
    }
    public void setIntelligence(int intelligence) {
        this.intelligence = intelligence;
    }
    public void setPerception(int perception) {
        this.perception = perception;
    }
    public void setAgility(int agility) {
        this.agility = agility;
    }
    public void setSpirit(int spirit) {
        this.spirit = spirit;
    }
    public void setCritChance(double critChance) {
        this.critChance = critChance;
    }
    public void setCritDmg(double critDmg) {
        this.critDmg = critDmg;
    }
    public void setMagicResist(double magicResist) {
        this.magicResist = magicResist;
    }
    public void setMagicDmg(double magicDmg) {
        this.magicDmg = magicDmg;
    }
    public void setPlayerLevel(int playerLevel) {
        this.playerLevel = playerLevel;
    }
    public void setUla(int ula) {
        this.ula = ula;
    }
    public void setAttPoints(int attPoints) {
        this.attPoints = attPoints;
    }
    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    public void checkLevelUp() {
        if (exp >= maxExp) {
            exp = 0;
            playerLevel += 1;
            attPoints += 5;

            maxExp = (int) (maxExp * 1.75);
        }
    }
}
