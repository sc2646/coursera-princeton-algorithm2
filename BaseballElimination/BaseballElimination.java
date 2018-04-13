package BaseballElimination;

import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FordFulkerson;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;


public class BaseballElimination {
    private int numberOfTeams;
    private int[] wins;
    private int[] losses;
    private int[] remains;
    private int[][] playingAgainst;
    private Map<String, Integer> teamNamesToId;
    private Map<Integer, String> idToTeamNames;

    // create a baseball division from given filename in format specified below
    public BaseballElimination(String filename){
        if(filename == null) throw new IllegalArgumentException();

        In input = new In(filename);

        this.numberOfTeams = input.readInt();
        this.wins = new int[this.numberOfTeams];
        this.losses = new int[this.numberOfTeams];
        this.remains = new int[this.numberOfTeams];
        this.playingAgainst = new int[this.numberOfTeams][this.numberOfTeams];
        this.teamNamesToId = new HashMap<String, Integer>();
        this.idToTeamNames = new HashMap<Integer, String>();

        int i = 0;
        while(!input.isEmpty()) {
            int teamId = i;
            String teamName = input.readString();
            this.teamNamesToId.put(teamName, teamId);
            this.idToTeamNames.put(teamId, teamName);
            this.wins[i] = input.readInt();
            this.losses[i] = input.readInt();
            this.remains[i] = input.readInt();
            for (int numOfGames = 0; numOfGames < playingAgainst[i].length; numOfGames++) {
                playingAgainst[i][numOfGames] = input.readInt();
            }
            i++;

        }

        input.close();
    }

    // number of teams
    public int numberOfTeams(){
        return numberOfTeams;
    }

    // all teams
    public Iterable<String> teams(){
        return teamNamesToId.keySet();
    }

    // number of wins for given team
    public int wins(String team){
        if(team==null) throw new IllegalArgumentException();
        if(!teamNamesToId.containsKey(team)) throw new IllegalArgumentException();
        return wins[teamNamesToId.get(team)];
    }

    // number of losses for given team
    public int losses(String team){
        if(team==null) throw new IllegalArgumentException();
        if(!teamNamesToId.containsKey(team)) throw new IllegalArgumentException();
        return losses[teamNamesToId.get(team)];
    }

    // number of remaining games for given team
    public int remaining(String team){
        if(team==null) throw new IllegalArgumentException();
        if(!teamNamesToId.containsKey(team)) throw new IllegalArgumentException();
        return remains[teamNamesToId.get(team)];
    }

    // number of remaining games between team1 and team2
    public int against(String team1, String team2){
        if(team1 == null || team2 == null) throw new IllegalArgumentException();
        if(!teamNamesToId.containsKey(team1) || !teamNamesToId.containsKey(team2)) throw new IllegalArgumentException();
        return playingAgainst[teamNamesToId.get(team1)][teamNamesToId.get(team2)];
    }

    // is given team eliminated?
    public boolean isEliminated(String team){
        if(team == null || !teamNamesToId.containsKey(team)) throw new IllegalArgumentException();

        // check for trivial elimination
        int toElim = teamNamesToId.get(team);
        for(int i=0; i<numberOfTeams; i++){
            if(wins[toElim] + remains[toElim] - wins[i] < 0) return true;
        }

        FlowNetwork fn = createFlowNetwork(toElim);

        int source = fn.V() - 2;
        int target = fn.V() - 1;

        FordFulkerson ff = new FordFulkerson(fn, source, target);

        for(FlowEdge e: fn.adj(source)){
            if(e.flow() != e.capacity()) return true;
        }
        return false;
    }


    private FlowNetwork createFlowNetwork(int teamId){

        // calculate the number of matches
        int numOfGames = numberOfTeams() * (numberOfTeams() - 1) / 2;

        // # of matches + teams (without x) + source and sink
        int numOfVerticies = numOfGames + numberOfTeams() + 2;

        // assign source
        int source = numOfGames + numberOfTeams();

        // assign sink to the last id
        int target = source + 1;

        FlowNetwork fn = new FlowNetwork(numOfVerticies);

        int nodeId = 0;
        for(int i=0; i < numberOfTeams(); i++){
            for(int j=i+1;j < numberOfTeams();j++){
                if(i==j) continue;
                // source to match
                fn.addEdge(new FlowEdge(source, nodeId, playingAgainst[i][j]));

                // match to i
                fn.addEdge(new FlowEdge(nodeId, numOfGames + i, Integer.MAX_VALUE));

                // match to j
                fn.addEdge(new FlowEdge(nodeId, numOfGames + j, Integer.MAX_VALUE));

                nodeId++;
            }
            // team node to target
            fn.addEdge(new FlowEdge(numOfGames+i, target, Math.max(0, wins[teamId]+remains[teamId]-wins[i])));
        }

        return fn;
    }

    // subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team){
        if(team == null) throw new IllegalArgumentException();
        if(!this.teamNamesToId.containsKey(team)) throw new java.lang.IllegalArgumentException();
        List<String> certificateElim = new ArrayList<String>();
        int toElim = this.teamNamesToId.get(team);
        if(isEliminated(team)) {
            for (int i = 0; i < this.numberOfTeams; i++) {
                if (wins[toElim] + remains[toElim] - wins[i] < 0) {
                    certificateElim.add(idToTeamNames.get(i));
                }
            }
            if (certificateElim.size() > 0) return certificateElim;

            FlowNetwork fn = createFlowNetwork(toElim);

            int source = fn.V() - 2;
            int target = fn.V() - 1;
            int numOfMatches = this.numberOfTeams * (this.numberOfTeams - 1) / 2;

            FordFulkerson ff = new FordFulkerson(fn, source, target);

            for (int i = numOfMatches; i < source; i++) {
                if (ff.inCut(i)) {
                    String name = this.idToTeamNames.get(i - numOfMatches);
                    certificateElim.add(name);
                }
            }
            return certificateElim;
        }
        return null;
    }

    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            }
            else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }


}
