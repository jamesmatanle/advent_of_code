import functools


def parse_line(line, expand):
    if expand:
        return (tuple('?'.join([line.split()[0] for _ in range(5)])),
                tuple([int(x) for _ in range(5) for x in line.split()[1].split(',')]))
    return (tuple(line.split()[0]),
            tuple([int(x) for x in line.split()[1].split(',')]))


def solve(A, expand):
    # @util.viz
    @functools.cache
    def fn(B, C, breakable):
        if not C:
            return 1 if '#' not in B else 0
        if len(B) < C[0]:
            return 0
        if B[0] == '#':  # must break
            if not breakable:
                return 0
            if not all(B[k] in '?#' for k in range(C[0])):
                return 0
            return fn(B[C[0]:], C[1:], False)
        if B[0] == '?':
            if breakable and all(B[k] in '?#' for k in range(C[0])):
                return (fn(B[C[0]:], C[1:], False) + fn(B[1:], C, True))
        return fn(B[1:], C, True)
    return sum(fn(parse_line(line, expand)[0],
                  parse_line(line, expand)[1],
                  True)
               for line in A)


def part1(A):
    return solve(A, False)


def part2(A):
    return solve(A, True)


TEST = """
???.### 1,1,3
.??..??...?##. 1,1,3
?#?#?#?#?#?#?#? 1,3,1,6
????.#...#... 4,1,1
????.######..#####. 1,6,5
?###???????? 3,2,1
""".strip().splitlines()

IN = open('day12_input.txt').read().splitlines()

print('part1 test:', part1(TEST))  # => 21
print('part1:', part1(IN))  # => 7402

print('part2 test:', part2(TEST))  # => 525152
print('part2:', part2(IN))  # => 3384337640277
