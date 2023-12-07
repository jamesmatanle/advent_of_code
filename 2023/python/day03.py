import re


def part1(A):
    def has_symbol_neighbor(A, i, j):
        return any([(i+p, j+q) for p in [1, 0, -1] for q in [1, 0, -1]
                    if -1 < i + p < M and -1 < j + q < N
                    and A[i+p][j+q] not in '1234567890.'])
    M, N = len(A), len(A[0])
    return sum([
        int(m.group())
        for i in range(M)
        for m in re.finditer(r'\d+', A[i])
        if any(has_symbol_neighbor(A, i, j) for j in range(m.start(), m.end()))
    ])


def part2(A):
    def star_neighbors(A, i, j):
        return [(i+p, j+q) for p in [1, 0, -1] for q in [1, 0, -1]
                if -1 < i + p < M and -1 < j + q < N and A[i+p][j+q] == '*']
    M, N = len(A), len(A[0])
    STARS = {}
    for i in range(M):
        for m in re.finditer(r'\d+', A[i]):
            starnei = set([coord
                           for j in range(m.start(), m.end())
                           for coord in star_neighbors(A, i, j)])
            for coord in starnei:
                STARS.setdefault(coord, []).append(int(m.group()))
    return sum(v[0]*v[1] for v in STARS.values() if len(v) == 2)


TEST = """
467..114..
...*......
..35..633.
......#...
617*......
.....+.58.
..592.....
......755.
...$.*....
.664.598..
""".strip().splitlines()


IN = open('day03_input.txt').read().splitlines()

print('part1 test:', part1(TEST))  # 4361
print('part1:', part1(IN))

print('part2 test:', part2(TEST))  # 467835
print('part2:', part2(IN))
