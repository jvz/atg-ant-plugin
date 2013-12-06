package atgant;

import java.util.ArrayList;

public abstract class ModuleFilter {

    public static ModuleFilter parseStringList(String filterListString) {
        final ArrayList<ModuleFilter> filters = new ArrayList<ModuleFilter>();
        String[] filterStringArray = filterListString.split("[,;:]");
        for (String filterString : filterStringArray) {
            filters.add(parseString(filterString.trim()));
        }
        return new ModuleFilter() {
            @Override
            public boolean match(AtgModule m) {
                for (ModuleFilter mf : filters) {
                    if (mf.match(m)) {
                        return true;
                    }
                }
                return false;
            }
        };
    }

    private static ModuleFilter parseString(final String filterString) {
        if (filterString.endsWith("**")) {
            final String prefix = filterString.substring(0, filterString.length() - 2);
            return new ModuleFilter() {
                @Override
                public boolean match(AtgModule m) {
                    return m.getName().startsWith(prefix);
                }
            };
        }
        else if (filterString.endsWith("*")) {
            final String prefix = filterString.substring(0, filterString.length() - 1);
            return new ModuleFilter() {
                @Override
                public boolean match(AtgModule m) {
                    if (m.getName().startsWith(prefix)) {
                        if (!m.getName().substring(prefix.length()).contains(".")) {
                            return true;
                        }
                    }
                    return false;
                }
            };
        }
        else {
            return new ModuleFilter() {
                @Override
                public boolean match(AtgModule m) {
                    return m.getName().equals(filterString);
                }
            };
        }
    }

    public abstract boolean match(AtgModule m);

}
