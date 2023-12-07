import re


def part1(A):
    def has_symbol_neighbor(A, i, j):
        return any([(i+p, j+q) for p in [1, 0, -1] for q in [1, 0, -1]
                    if -1 < i + p < M and -1 < j + q < N and A[i+p][j+q] not in '1234567890.'])
    M, N = len(A), len(A[0])
    return sum([
        int(m.group())
        for i in range(M)
        for m in re.finditer(r'\d+', A[i])
        if any(has_symbol_neighbor(A, i, j) for j in range(m.start(), m.end()))
    ])


def part2(A):
    return
