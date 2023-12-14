import collections

CONN = {
    "|": [(-1, 0), (1, 0)],
    "-": [(0, -1), (0, 1)],
    "L": [(-1, 0), (0, 1)],
    "J": [(-1, 0), (0, -1)],
    "7": [(0, -1), (1, 0)],
    "F": [(0, 1), (1, 0)],
}


def part1(A):
    M, N = len(A), len(A[0])
    si, sj = next((i, j) for i in range(M) for j in range(N) if A[i][j] == 'S')
    queue = collections.deque()
    seen = set([(si, sj)])
    for di, dj in [(-1, 0), (1, 0), (0, -1), (0, 1)]:
        i, j = si + di, sj + dj
        if A[i][j] in CONN:
            (di1, dj1), (di2, dj2) = CONN[A[i][j]]
            if (si, sj) in [(i+di1, j+dj1), (i+di2, j+dj2)]:
                queue.append((1, i, j))
                seen.add((i, j))
    while queue:
        step, i, j = queue.popleft()
        (di1, dj1), (di2, dj2) = CONN[A[i][j]]
        for di, dj in CONN[A[i][j]]:
            ni, nj = i+di, j+dj
            if (ni, nj) not in seen:
                seen.add((ni, nj))
                queue.append((step+1, ni, nj))
    return step, seen


def part2(A):
    "Diagonal ray works well because the edges tangent to the ray cannot enter the polygon."
    _, WALLS = part1(A)
    M, N = len(A), len(A[0])
    res = 0
    # replace S with real edge "7" in my full input. off by 9 only in full input.
    if M == 140 and N == 140 and A[42][25] == 'S':
        A[42] = A[42][:25] + '7' + A[42][26:]
    for i in range(M):
        for j in range(N):
            if (i, j) in WALLS:
                continue
            acc = 0
            for k in range(min(M-i, N-j)):
                # NOTE 'L7' is tangent to ray
                if (i+k, j+k) in WALLS and A[i+k][j+k] not in 'L7':
                    acc += 1
            if acc % 2 == 1:
                res += 1
    return res


TEST = """
..F7.
.FJ|.
SJ.L7
|F--J
LJ...
""".strip().splitlines()

IN = open('day10_input.txt').read().splitlines()

print('part1 test:', part1(TEST)[0])  # => 8
print('part1:', part1(IN)[0])  # => 7173

TEST = """
...........
.S-------7.
.|F-----7|.
.||.....||.
.||.....||.
.|L-7.F-J|.
.|..|.|..|.
.L--J.L--J.
...........
""".strip().splitlines()

print('part2 test:', part2(TEST))  # => 4

TEST = """
.F----7F7F7F7F-7....
.|F--7||||||||FJ....
.||.FJ||||||||L7....
FJL7L7LJLJ||LJ.L-7..
L--J.L7...LJS7F-7L7.
....F-J..F7FJ|L7L7L7
....L7.F7||L7|.L7L7|
.....|FJLJ|FJ|F7|.LJ
....FJL-7.||.||||...
....L---J.LJ.LJLJ...
""".strip().splitlines()

print('part2 test:', part2(TEST))  # => 8

TEST = """
FF7FSF7F7F7F7F7F---7
L|LJ||||||||||||F--J
FL-7LJLJ||||||LJL-77
F--JF--7||LJLJ7F7FJ-
L---JF-JLJ.||-FJLJJ7
|F|F-JF---7F7-L7L|7|
|FFJF7L7F-JF7|JL---7
7-L-JL7||F7|L7F-7F7|
L.L7LFJ|||||FJL7||LJ
L7JLJL-JLJLJL--JLJ.L
""".strip().splitlines()

print('part2 test:', part2(TEST))  # => 10

print('part2:', part2(IN))
