import java.util.HashMap;
import java.util.Map;

public class SlayerMonsterMapsScript {

    private static final Map<Integer, String[]> monsterMap = new HashMap<>();

    static {
        monsterMap.put(1, new String[] {"Penguin"});
        monsterMap.put(5, new String[] {"Luigi"});
        monsterMap.put(7, new String[] {"Crash Bandicoot"});
        monsterMap.put(8, new String[] {"ThugBob"});
        monsterMap.put(9, new String[] {"Mario"});
        monsterMap.put(13, new String[] {"Groudon"});
        monsterMap.put(18, new String[] {"Guardians of Amorth"});
        monsterMap.put(22, new String[] {"Undead Lancelot"});
        monsterMap.put(23, new String[] {"Tinky", "Poe", "Lala", "Dipsy"});
        monsterMap.put(24, new String[] {"Daggett", "Norbert"});
        monsterMap.put(25, new String[] {"Timon", "Pumbaa"});
        monsterMap.put(26, new String[] {"Buzz Lightyear"});
        monsterMap.put(27, new String[] {"Dexter", "DeeDee"});
        monsterMap.put(30, new String[] {"CatDog"});
        monsterMap.put(31, new String[] {"Rocco", "Heffer", "Big Head"});
        monsterMap.put(32, new String[] {"Edd", "eddy"});
        monsterMap.put(33, new String[] {"Ren", "Stimpy"});
        monsterMap.put(34, new String[] {"Kim Possible"});
        monsterMap.put(35, new String[] {"Beavis", "Butthead"});
        monsterMap.put(36, new String[] {"Chucky", "Tommy Pickles"});
        monsterMap.put(37, new String[] {"Undead War General"});
        monsterMap.put(39, new String[] {"Jimmy Neutron", "Carl Wheezer"});
        monsterMap.put(40, new String[] {"Demon Conjurer"});
        monsterMap.put(41, new String[] {"Tainted Warmaster"});
        monsterMap.put(42, new String[] {"Helwyr Elf"});
        monsterMap.put(43, new String[] {"Ickis", "Krumm"});
        monsterMap.put(44, new String[] {"Ancient Guardian"});
        monsterMap.put(45, new String[] {"Ancient Wizard"});
        monsterMap.put(46, new String[] {"Ancient Crusader"});
        monsterMap.put(47, new String[] {"Corrupted Priest"});
        monsterMap.put(48, new String[] {"Demonic Spellcaster"});
        monsterMap.put(49, new String[] {"Sully"});
        monsterMap.put(50, new String[] {"Hormone"});
        monsterMap.put(65,new String[] {"Poseidon"});
        monsterMap.put(67,new String[] {"Masterwork Warrior"});
        monsterMap.put(71, new String[] {"Bugs Bunny"});
        monsterMap.put(72, new String[] {"Wile E Coyote"});
        monsterMap.put(73, new String[] {"Daffy Duck"});
        monsterMap.put(74, new String[] {"Foghorn Leghorn"});
        monsterMap.put(75, new String[] {"Sylvester"});
        monsterMap.put(76, new String[] {"Tasmanian Devil"});
    }

    public static String[] getNames(int id) {
        return monsterMap.get(id);
    }
}