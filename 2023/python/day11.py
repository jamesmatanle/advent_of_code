def solve(A, expansion):
    def manhattan_distance_expansion(x1, y1, x2, y2):
        x1, x2 = sorted([x1, x2])
        y1, y2 = sorted([y1, y2])
        return (x2 - x1 + y2 - y1
                + sum(expansion for i in EMPTY_ROW if x1 <= i <= x2)
                + sum(expansion for j in EMPTY_COL if y1 <= j <= y2))
    M, N = len(A), len(A[0])
    GAL = [(i, j) for i in range(M) for j in range(N) if A[i][j] == '#']
    EMPTY_ROW = [i for i in range(M) if all(c == '.' for c in A[i])]
    EMPTY_COL = [j for j in range(N) if all(c == '.' for c in [A[i][j] for i in range(M)])]
    return sum(manhattan_distance_expansion(x1, y1, x2, y2)
               for i, (x1, y1) in enumerate(GAL)
               for x2, y2 in GAL[i:])


def part1(A):
    return solve(A, 1)


def part2(A):
    return solve(A, 999999)


TEST = """
...#......
.......#..
#.........
..........
......#...
.#........
.........#
..........
.......#..
#...#.....
""".strip().splitlines()


IN = open('day11_input.txt').read().splitlines()

print(part1(TEST))  # => 374
print(part1(IN))  # => 9370588

print(part2(IN))  # => 746207878188
