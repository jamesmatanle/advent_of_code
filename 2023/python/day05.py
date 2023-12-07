import re


def part1(S):
    A = [[[int(x) for x in re.findall(r'\d+', s)]
          for s in section.split(':')[1].strip().splitlines()]
         for section in S.split('\n\n')]
    acc = A[0][0]
    for m in A[1:]:
        for i, x in enumerate(acc):
            for dest, source, length in m:
                if source <= x < source + length:
                    acc[i] = dest + (x - source)
    return min(acc)


def part2(A):
    return


TEST = """
seeds: 79 14 55 13

seed-to-soil map:
50 98 2
52 50 48

soil-to-fertilizer map:
0 15 37
37 52 2
39 0 15

fertilizer-to-water map:
49 53 8
0 11 42
42 0 7
57 7 4

water-to-light map:
88 18 7
18 25 70

light-to-temperature map:
45 77 23
81 45 19
68 64 13

temperature-to-humidity map:
0 69 1
1 0 69

humidity-to-location map:
60 56 37
56 93 4
""".strip()

IN = open('day05_input.txt').read()

print('part1 test:', part1(TEST))  # 35
print('part1:', part1(IN))

print('part2 test:', part2(TEST))  # ?
print('part2:', part2(IN))


