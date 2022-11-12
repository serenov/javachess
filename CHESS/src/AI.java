import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class AI {

    public static class Moves{
        String move;
        int score;
        public Moves(String move){
            score = 0;
            this.move = move;
        }
    }

    static class sortByScore implements Comparator<Moves> {
        public int compare(Moves a, Moves b)
        {
            return b.score - a.score;
        }
    }

    static int status;
    static Hashtable<Long, Integer> ht = new Hashtable<>();
    static Hashtable<Long, Integer[]> hft = new Hashtable<>();
    static bitboard b;
    static String bestmove;
    static final String[] app = new String[]{
        "Q", "R", "B", "N",
        "q", "r", "b", "n"
    };

    static final int infinity = 10000;
    static final int notready = -100000;
    static int limit = 5;
    static int totalcount;
    static int movescnt = 0;

    public static String stringify(int sel, int des){
        char x = (char)('a' + (sel % 8));
        char y = (char)('1' + (sel / 8));
        char dx = (char)('a' + (des % 8));
        char dy = (char)('1' + (des / 8));

        return new String(new char[]{x, y, dx, dy});
    }

    public static int minimax(int depth){
        int turn = b.turn;
        int f = (turn == 1)? -1: 1;
        if((status = b.isOver()) != 0){
            if(status == 3) return -1 * f * (infinity - depth);
            else return 0;
        }
        if(depth > limit) return b.eval();

        long piece;
        long pmoves;
        long selection;
        long destination;
        int pos;
        int promcnt = 0;
        int count;
        int besteval = -1 * f * infinity;
        int curreval;

        for(int i = 0; i < 6; i++){
            piece = b.getp(6 * turn + i);
            for(int j = bitboard.countBits(piece); j > 0; j--){
                pos = bitboard.getIndex(piece);
                selection = 1l << pos;
                pmoves = b.getMoves(6 * turn + i, pos % 8, pos / 8);
                count = bitboard.countBits(pmoves);
                for(int k = 0; k < count; k++){
                    destination = 1l << bitboard.getIndex(pmoves);
                    movescnt++;
                    if(promcnt == 0)
                    if(b.isPromotion(turn * 6 + i, destination)){
                        promcnt = 4;
                        b.promoteToID = turn * 6 + 4;
                        k -= 3;
                    }

                    b.history(6 * turn + i, selection, destination);
                    b.move(6 * turn + i, selection, destination);
                    b.moves++;
                    b.alternate();
                    long hash = zHash.genHash(b) + depth;

                    if(ht.containsKey(hash))curreval = ht.get(hash);
                    else {
                        curreval = minimax(depth + 1);
                        ht.put(hash, curreval);
                    }
                    //curreval = minimax(depth + 1);
                    b.undo(--b.moves);
                    
                    
                    
                    if((f * curreval) > (f * besteval)){
                        besteval = curreval;
                        if(depth == 0) 
                        bestmove = stringify(bitboard.getIndex(selection), bitboard.getIndex(destination));
                    }

                    if(promcnt != 0){
                        promcnt--;
                        b.promoteToID = turn * 6 + promcnt;
                    }
                    if(promcnt == 0)pmoves &= ~destination;
                }
                piece &= ~(1l << pos);
            }
        }
        return besteval;
    }


    public static int AlphaBetapruning(int depth, int alpha, int beta){

        int f = (b.turn == 1)? -1: 1;

        if((status = b.isOver()) != 0){
            if(status == 3) return -1 * f * (infinity - depth);
            else return 0;
        }
        if(depth > limit) return b.eval();

        long piece, pmoves, selection, destination;

        int pos, count, promcnt = 0;

        int besteval = (b.turn == 1)? beta: alpha, curreval;

        for(int i = 0; i < 6; i++){

            piece = b.getp(6 * b.turn + i);

            for(int j = bitboard.countBits(piece); j > 0; j--){

                pos = bitboard.getIndex(piece);
                selection = 1l << pos;
                pmoves = b.getMoves(6 * b.turn + i, pos % 8, pos / 8);
                count = bitboard.countBits(pmoves);

                for(int k = 0; k < count; k++){
                    movescnt++;
                    destination = 1l << bitboard.getIndex(pmoves);

                    if(promcnt == 0)
                    if(b.isPromotion(b.turn * 6 + i, destination)){
                        promcnt = 4;
                        b.promoteToID = b.turn * 6 + 4;
                        k -= 3;
                    }

                    b.history(6 * b.turn + i, selection, destination);
                    b.move(6 * b.turn + i, selection, destination);
                    b.moves++;
                    b.alternate();

                    long hash = zHash.genHash(b) + depth;

                    if(ht.containsKey(hash))curreval = ht.get(hash);
                    else {
                        curreval = AlphaBetapruning(depth + 1, alpha, beta);
                        ht.put(hash, curreval);
                    }
                    
                    //AlphaBetapruning(depth + 1, alpha, beta);

                    b.undo(--b.moves);

                    if((f * curreval) > (f * besteval)){
                        besteval = curreval;
                        if(depth == 0){ 
                            bestmove = stringify(bitboard.getIndex(selection), bitboard.getIndex(destination));
                            System.out.println(bestmove + ": " + curreval + " for depth: " + limit);
                        }   
                        if(b.turn == 1) beta = besteval;
                        else alpha = besteval;
                    } 

                    if(alpha >= beta){
                        j = 0;
                        i = 6;
                        break;
                    }
                    if(promcnt != 0){
                        promcnt--;
                        b.promoteToID = b.turn * 6 + promcnt;
                    }

                    if(promcnt == 0) pmoves &= ~destination;
                }

                piece &= ~(1l << pos);

            }
        }
        return besteval;
    }
    
    public static void orderMoves(){
        
        Moves[] m = moveGenerator();
        int promcnt = 0;

        for(Moves mt: m){
            switch(mt.move.charAt(4)){
                case 'p':
                    mt.score = 50 - (((promcnt++) * 10) % 50);
                    break;
                case 'c':
                    mt.score += 10 * (mt.move.charAt(5) - '0');
            }
        }
        Arrays.sort(m, new sortByScore());
        for(Moves mt: m){
            System.out.println(mt.score);
            System.out.println(mt.move);
        }

        System.out.println(b);
        //return null;
    }
    
    public static Moves[] moveGenerator(){
        long selection;
        long destination;
        long piece;
        long pmoves;
        int spos;
        int dpos;
        String AC;
        ArrayList<Moves> lis = new ArrayList<Moves>();

        for(int i = 0; i < 6; i++){
            piece = b.getp(6 * b.turn + i);
            for(int j = bitboard.countBits(piece); j > 0; j--){
                spos = bitboard.getIndex(piece);
                selection = 1l << spos;
                pmoves = b.getMoves(b.turn * 6 + i, spos % 8, spos / 8);
                for(int k = bitboard.countBits(pmoves); k > 0; k--){
                    dpos = bitboard.getIndex(pmoves);
                    destination = 1l << dpos;
                    if(b.isPromotion(6 * b.turn + i, destination)){
                        for(int l = 0; l < 4; l++)
                            lis.add(new Moves(stringify(spos, dpos) + "p" + app[4 * b.turn + l]));
                    }
                    else{
                        if(b.isCastling(i, destination) && i * (i - 6) == 0){
                            AC = "C";
                        }
                        else if(b.isCapture(i, destination)){
                            int ID = b.getPiece(destination);
                            if(ID == 12) ID = 5;
                            AC = "c" + Integer.toString(6 - (ID % 6));
                            ID = b.getPiece(selection) % 6;
                        }
                        else AC = " ";
                        lis.add(new Moves(stringify(spos, dpos) + AC));
                    }
                    pmoves &= ~destination;
                }
                piece &= ~selection;
            }
        }
        Moves[] m = new Moves[lis.size()];
        m = lis.toArray(m);
        return m;
    }

    public static String AIplayer(){
        totalcount = Perft.countmoves(b);
        //minimax(0);
        movescnt = 0;
        System.out.println(" eval: " + AlphaBetapruning(0, -infinity, infinity));
        System.out.println("bestmove: " + bestmove);
        System.out.println(movescnt + " is count \n\n");

        return bestmove;
    }

    public static void main(String args[]){
        zHash.INIT();
        int s;

        try{
            limit = Integer.parseInt(args[6]);
            s = Integer.parseInt(args[7]);
            b = new bitboard(args);
            
        }
        catch(Exception e){
            System.err.println("taking default fen string and limit");
            b = new bitboard();
            limit = 5;
            s = 0;
        }

        b.INIT();
        b.bitboardsInit();
        totalcount = Perft.countmoves(b);


        // if(s == 1)System.out.println("\nEvaluation: " + AlphaBetapruning(0, -infinity, infinity));
        // else System.out.println("\nEvaluation: " + minimax(0));

        // System.out.println("Best move: " + bestmove);
        // System.out.println("counter: " + movescnt);
        orderMoves();

        movescnt = 0;
        b.bitDisplay();
    }
}
