def parse(S):
    return [grid.splitlines() for grid in S.split('\n\n')]


def part1(S):
    def find_hori(A):
        for i in range(1, len(A)):
            rows_up, rows_down = A[i-1::-1], A[i:]
            L = min(len(rows_up), len(rows_down))
            if rows_up[:L] == rows_down[:L]:
                return i
        return 0
    return sum(100 * find_hori(A) + find_hori(list(zip(*A))) for A in parse(S))


def part2(S):
    def find_hori(A):
        for i in range(1, len(A)):
            rows_up, rows_down = A[i-1::-1], A[i:]
            if 1 == sum(c1 != c2
                        for row_up, row_down in zip(rows_up, rows_down)
                        for c1, c2 in zip(row_up, row_down)):
                return i
        return 0
    return sum(100 * find_hori(A) + find_hori(list(zip(*A))) for A in parse(S))


TEST = """
#.##..##.
..#.##.#.
##......#
##......#
..#.##.#.
..##..##.
#.#.##.#.

#...##..#
#....#..#
..##..###
#####.##.
#####.##.
..##..###
#....#..#
""".strip()

IN = open('day13_input.txt').read()

print('part1 test:', part1(TEST))  # => 405
print('part1:', part1(IN))  # => 37025

print('part2 test:', part2(TEST))  # => 400
print('part2:', part2(IN))  # => 32854
