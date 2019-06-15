package ch.juventus.yatzi.game.board.score;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Ranking implements Comparable {

    private Integer rank;
    private String userName;
    private Integer total;

    /**
     * Rankings should be compared by its total value
     * @param o Object to compare. It have to be an instance of Ranking.
     * @return 1 or -1
     */
    @Override
    public int compareTo(Object o) {
        int compareTotal = ((Ranking)o).getTotal();
        return compareTotal - this.total;
    }
}
