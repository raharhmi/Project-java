public class Player {

    private String id;
    private int score;

    public Player(String id){
        this.id = id;
        this.score= 0;
    }

    public String getId() {
        return id;
    }

    public int getScore() {
        return score;
    }

    public String setId(String id){
        return id;
    }

    public int setScore(int score){
        return score;
    }

    public void addScore(int points){
        this.score += points;
    }
@Override
    public String toString(){
        return "id: " + id + "score: " + score;
    }

}