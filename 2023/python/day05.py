import collections
import re


def part1(S):
    A = [[[int(x) for x in re.findall(r'\d+', s)]
          for s in section.split(':')[1].strip().splitlines()]
         for section in S.split('\n\n')]
    acc = A[0][0]
    for m in A[1:]:
        for i, x in enumerate(acc):
            for v, u, uvlen in m:
                if u <= x < u + uvlen:
                    acc[i] = v + (x - u)
    return min(acc)


# part 2 - an interval can intersect with multiple map intervals.
# range comparisons...
# can clean this up...

def part2(S):
    def intersection_results(mapi, xlo, xhi, vlo, ulo, uhi):
        """return results of intersection"""
        res = []
        if uhi <= xlo or xhi <= ulo:
            return res
        if xlo < ulo:
            res.append((mapi, xlo, ulo))
            xlo = ulo
        if uhi < xhi:
            res.append((mapi, uhi, xhi))
            xhi = uhi
        res.append((mapi + 1, xlo + (vlo - ulo), xhi + (vlo - ulo)))
        return res
    A = [[[int(x) for x in re.findall(r'\d+', s)]
          for s in section.split(':')[1].strip().splitlines()]
         for section in S.split('\n\n')]
    q = collections.deque(
        [(1, A[0][0][i], A[0][0][i] + A[0][0][i+1])
         for i in range(0, len(A[0][0]), 2)])
    res = float('inf')
    while q:
        mapi, xlo, xhi = q.popleft()
        if mapi == len(A):
            res = min(res, xlo)
            continue
        nxt = []
        for vlo, ulo, ulen in A[mapi]:
            nxt = intersection_results(mapi, xlo, xhi, vlo, ulo, ulo+ulen)
            if nxt:
                q += nxt
                break
        if not nxt:  # no matches
            q.append((mapi+1, xlo, xhi))
    return res


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

print('part2 test:', part2(TEST))  # 46
print('part2:', part2(IN))


